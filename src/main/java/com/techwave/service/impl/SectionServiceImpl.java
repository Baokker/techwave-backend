package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.techwave.entity.*;
import com.techwave.entity.dto.*;
import com.techwave.entity.vo.*;
import com.techwave.mapper.*;
import com.techwave.service.CollectService;
import com.techwave.service.PostService;
import com.techwave.service.SectionService;
import com.techwave.service.ThreadService;
import com.techwave.utils.TCode;
import com.techwave.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 版块服务的实现
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 14:02
 * @since JDK18
 */
@Service
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private SubSectionMapper subSectionMapper;
    @Autowired
    private SectionAndSubSectionMapper sectionAndSubSectionMapper;
    @Autowired
    private CollectAndSectionMapper collectAndSectionMapper;
    @Autowired
    private CollectService collectService;
    @Autowired
    private PostService postService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ModeratorMapper moderatorMapper;
    @Override
    public Result getSectionData(Long userId, Long sectionId, Integer page, Integer perPage) {
        if (sectionId == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);

        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "该版块不存在，参数有误", null);
        }
        SectionDataVO sectionDataVO = new SectionDataVO();
        sectionDataVO.setName(section.getName());
        sectionDataVO.setAvatar(section.getAvatar());
        sectionDataVO.setCollectCount(section.getUserCount());

        if (userId != null) {
            sectionDataVO.setIsCollected(collectService.isUserCollectSection(userId, sectionId));
        } else {
            sectionDataVO.setIsCollected(false);
        }
        sectionDataVO.setSummary(section.getDescription());
        sectionDataVO.setSubSectionList(this.findSubSectionBySectionId(sectionId));
        sectionDataVO.setPostVOList(postService.findPostBySectionIdWithPage(sectionId, page, perPage));
        return Result.success(20000, "okk", sectionDataVO);
    }

    public Result getSectionDataById(Long sectionId){
        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "该版块不存在，参数有误", null);
        }
        SectionDataVO sectionDataVO = new SectionDataVO();
        sectionDataVO.setName(section.getName());
        sectionDataVO.setAvatar(section.getAvatar());
        sectionDataVO.setSubSectionList(this.findSubSectionBySectionId(sectionId));

        return Result.success(20000, "okk", sectionDataVO);
    }

    @Override
    public Section findSectionById(Long sectionId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getId, sectionId);
        queryWrapper.last("limit 1");
        return this.sectionMapper.selectOne(queryWrapper);
    }

    @Override
    public SubSection findSubSectionById(Long subsectionId) {
        LambdaQueryWrapper<SubSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubSection::getId, subsectionId);
        queryWrapper.last("limit 1");
        return this.subSectionMapper.selectOne(queryWrapper);
    }

    @Override
    public Result getPostsByTag(PostsWithTagDTO postsWithTagDTO) {
        Long subsectionId = postsWithTagDTO.getSubsectionId();
        Integer page = postsWithTagDTO.getPage();
        Integer perPage = postsWithTagDTO.getPerPage();

        if (subsectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);

        List<PostDataVO> postList = postService.findPostBySectionIdAndSubSectionId(subsectionId, page, perPage);

        SectionPostsVO sectionPostsVO = new SectionPostsVO();
        sectionPostsVO.setPostDataVOList(postList);

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subsection_id", subsectionId);
        sectionPostsVO.setTotal(Math.toIntExact(postMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostsVO);
    }

    public Result getUserSectionsBySectionId(Long userId, Long sectionId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getModeratorId, userId);
        queryWrapper.eq(Section::getId, sectionId);
        List<Section> sections = sectionMapper.selectList(queryWrapper);
        List<MySectionsVO> mySectionsVOList = copyToMySectionsVO(sections);
        Map<String, Object> map = new HashMap<>();
        map.put("sectionInfo", mySectionsVOList);
        return Result.success(map);
    }

    public Result changeSectionName(SectionNameDTO sectionNameDTO) {
        Long sectionId = sectionNameDTO.getSectionId();
        String sectionName = sectionNameDTO.getName();
        Section section = this.findSectionById(sectionId);
        section.setName(sectionName);
        sectionMapper.updateById(section);
        return Result.success(20000,"okk",null);
    }

    @Override
    public List<SubSection> findSubSectionBySectionId(Long sectionId) {
        return subSectionMapper.findSubSectionBySectionId(sectionId);
    }

    @Override
    public Result collectSection(Long userId, Integer page, Integer perPage) {
        LambdaQueryWrapper<CollectionAndSection> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(CollectionAndSection::getUserId, userId);
        Integer total = Math.toIntExact(collectAndSectionMapper.selectCount(countWrapper));

        if (total == 0) {
            List<ForumSectionVO> forumSectionVOS = new ArrayList<>();
            return Result.fail(20000, "okk", forumSectionVOS);
        }

        LambdaQueryWrapper<CollectionAndSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndSection::getUserId, userId);
        queryWrapper.last("limit " + (page - 1) * perPage + "," + perPage);

        List<CollectionAndSection> collectionAndSections = collectAndSectionMapper.selectList(queryWrapper);

        List<Long> sectionIdList = new ArrayList<>();
        for (CollectionAndSection collectionAndSection :
                collectionAndSections) {
            sectionIdList.add(collectionAndSection.getSectionId());
        }

        LambdaQueryWrapper<Section> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(Section::getId, sectionIdList);
        List<Section> sections = sectionMapper.selectList(queryWrapper1);

        return Result.success(20000, "okk", new ForumSectionDTO(total, copyList(sections)));
    }

    @Override
    public Result hotSection(Integer page, Integer perPage) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Section::getPostCount);
        queryWrapper.last("limit " + (page - 1) * perPage + "," + perPage);

        List<Section> sections = sectionMapper.selectList(queryWrapper);

        LambdaQueryWrapper<Section> countWrapper = new LambdaQueryWrapper<>();
        Integer total = Math.toIntExact(sectionMapper.selectCount(countWrapper));

        return Result.success(20000, "okk", new ForumSectionDTO(total, copyList(sections)));
    }

    @Override
    public Result searchSection(Integer page, Integer perPage, String content) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Section::getPostCount);
        if (content != null && !content.equals("")) {
            queryWrapper.like(Section::getName, content);
        }
        queryWrapper.last("limit " + (page - 1) * perPage + "," + perPage);

        List<Section> sections = sectionMapper.selectList(queryWrapper);

        LambdaQueryWrapper<Section> countWrapper = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            countWrapper.like(Section::getName, content);
        }
        Integer total = Math.toIntExact(sectionMapper.selectCount(countWrapper));

        return Result.success(20000, "okk", new ForumSectionDTO(total, copyList(sections)));
    }

    @Override
    public void updateSectionByCollectCount(Long sectionId, boolean b) {
        Section section = this.findSectionById(sectionId);
        threadService.updateSectionByCollectCount(sectionMapper, section, b);
    }

    @Override
    public void updateSectionByPostCount(Long sectionId, boolean b) {
        Section section = this.findSectionById(sectionId);
        threadService.updateSectionByPostCount(sectionMapper, section, b);
    }

    @Override
    public Result getUserSections(Long userId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getModeratorId, userId);
        List<Section> sections = sectionMapper.selectList(queryWrapper);
        List<MySectionsVO> mySectionsVOList = copyToMySectionsVO(sections);
        Map<String, Object> map = new HashMap<>();
        map.put("sectionInfo", mySectionsVOList);
        return Result.success(map);
    }

    @Override
    public Result addSubSection(AddSubSectionDTO addSubSectionDTO) {
        String[] subsections = addSubSectionDTO.getSubsections();
        Long sectionId = addSubSectionDTO.getSectionId();
        if (subsections.length == 0) {
            return Result.fail(-1, "参数有误", null);
        }
        Boolean flag = false;
        for (String name :
                subsections) {
            if (this.isSubSectionLegal(sectionId, name)) {
                SubSection subSection = new SubSection();
                subSection.setName(name);
                subSectionMapper.insert(subSection);
                SectionAndSubSection sectionAndSubSection = new SectionAndSubSection();
                sectionAndSubSection.setSectionId(sectionId);
                sectionAndSubSection.setSubsectionId(subSection.getId());
                sectionAndSubSectionMapper.insert(sectionAndSubSection);
            } else {
                flag = true;
            }
        }
        if (flag) {
            return Result.success(20000, "重复名的版块未创建", null);
        }
        return Result.success(20000, "okk", null);
    }

    @Override
    public Result changeSectionIntro(ChangeIntroDTO changeIntroDTO) {
        Long sectionId = changeIntroDTO.getSectionId();
        String sectionIntro = changeIntroDTO.getDescription();
        Section section = this.findSectionById(sectionId);
        section.setDescription(sectionIntro);
        sectionMapper.updateById(section);
        return Result.success(20000, "okk", null);
    }

    @Override
    public Result getAllPostsInSection(SectionDataDTO sectionDataDTO) {
        Long sectionId = sectionDataDTO.getSectionId();
        Integer page = sectionDataDTO.getPage();
        Integer perPage = sectionDataDTO.getPerPage();

        if (sectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<PostDataVO> postList = postService.findPostBySectionIdWithPage(sectionId, page, perPage);

        SectionPostsVO sectionPostsVO = new SectionPostsVO();
        sectionPostsVO.setPostDataVOList(postList);

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        sectionPostsVO.setTotal(Math.toIntExact(postMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostsVO);
    }

    @Override
    public Result getPostsInSectionByContent(SectionSearchPostDTO sectionSearchPostDTO) {
        Long sectionId = sectionSearchPostDTO.getSectionId();
        Integer page = sectionSearchPostDTO.getPage();
        Integer perPage = sectionSearchPostDTO.getPerPage();
        String content = sectionSearchPostDTO.getContent();

        if (sectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<PostDataVO> postList = postService.findPostBySectionIdWithPageAndContent(sectionId, page, perPage, content);

        SectionPostsVO sectionPostsVO = new SectionPostsVO();
        sectionPostsVO.setPostDataVOList(postList);

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        if (content != null && !content.equals("")) {
            queryWrapper.like("title", content);
        }

        sectionPostsVO.setTotal(Math.toIntExact(postMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostsVO);
    }

    @Override
    public Result getPinnedPosts(Long sectionId) {
        if (sectionId == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<PostDataVO> postList = postService.findPinnedPostsBySectionId(sectionId);

        SectionPostsVO sectionPostsVO = new SectionPostsVO();
        sectionPostsVO.setPostDataVOList(postList);

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        queryWrapper.eq("is_pinned", 1);

        sectionPostsVO.setTotal(Math.toIntExact(postMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostsVO);
    }

    @Override
    public Result getHighlightedPostsInSectionWithPage(SectionDataDTO sectionDataDTO) {
        Long sectionId = sectionDataDTO.getSectionId();
        Integer page = sectionDataDTO.getPage();
        Integer perPage = sectionDataDTO.getPerPage();

        if (sectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = this.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<PostDataVO> postList = postService.findHighlightedPostBySectionIdWithPage(sectionId, page, perPage);

        SectionPostsVO sectionPostsVO = new SectionPostsVO();
        sectionPostsVO.setPostDataVOList(postList);

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        queryWrapper.eq("is_highlighted", 1);
        sectionPostsVO.setTotal(Math.toIntExact(postMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostsVO);
    }

    @Override
    public List<Long> findSectionIdsByModeratorId(Long userId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getModeratorId, userId);
        List<Section> sectionList = sectionMapper.selectList(queryWrapper);
        List<Long> sectionIdList = new ArrayList<>();
        for (Section section :
                sectionList) {
            sectionIdList.add(section.getId());
        }
        return sectionIdList;
    }

    private boolean isSubSectionLegal(Long sectionId, String name) {
        List<SubSection> subSectionList = findSubSectionBySectionId(sectionId);
        for (SubSection subsection :
                subSectionList) {
            if (Objects.equals(name, subsection.getName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Result createSection(Long userId, String sectionName, String image, String sectionIntro, String[] subsection) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getName, sectionName);
        queryWrapper.last("limit 1");
        Section section = sectionMapper.selectOne(queryWrapper);
        if (section != null) {
            return Result.fail(-1, "论坛里已有该版块", null);
        }
        Section section1 = new Section();
        section1.setName(sectionName);
        section1.setAvatar(image);
        section1.setDescription(sectionIntro);
        section1.setModeratorId(userId);
        sectionMapper.insert(section1);
        for (int i = 0; i < subsection.length; i++) {
            AddSubSectionDTO addSubSectionDTO = new AddSubSectionDTO(section1.getId(), subsection);
            this.addSubSection(addSubSectionDTO);
        }
        return Result.success(20000, "okk", null);
    }

    @Override
    public Result deleteSubSection(Long userId, Integer subsectionId) {
        LambdaQueryWrapper<SectionAndSubSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SectionAndSubSection::getSubsectionId, subsectionId);
        queryWrapper.last("limit 1");
        SectionAndSubSection sectionAndSubSection = sectionAndSubSectionMapper.selectOne(queryWrapper);

        Section section = this.findSectionById(sectionAndSubSection.getSectionId());
        if (!Objects.equals(userId, section.getModeratorId())) {
            return Result.fail(-1, "没有权限", null);
        }
        sectionAndSubSectionMapper.deleteById(sectionAndSubSection);

        SubSection subSection = this.findSubSectionById(Long.valueOf(subsectionId));
        subSectionMapper.deleteById(subSection);
        return Result.success(20000, "okk", null);
    }

    @Override
    public Result renameSubSection(RenameSubSectionDTO renameSubSectionDTO) {
        Long userId = renameSubSectionDTO.getUserId();
        Long subsectionId = renameSubSectionDTO.getSubsectionId();
        String name = renameSubSectionDTO.getName();

        LambdaQueryWrapper<SectionAndSubSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SectionAndSubSection::getSubsectionId, subsectionId);
        queryWrapper.last("limit 1");
        SectionAndSubSection sectionAndSubSection = sectionAndSubSectionMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<Section> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Section::getId, sectionAndSubSection.getSectionId());
        queryWrapper.last("limit 1");
        Section section = sectionMapper.selectOne(queryWrapper1);

        if (!Objects.equals(userId, section.getModeratorId())) {
            return Result.fail(-1, "没有权限", null);
        }
        List<SubSection> subSections = this.findSubSectionBySectionId(section.getId());
        for (SubSection sub :
                subSections) {
            if (Objects.equals(name, sub.getName())) {
                return Result.fail(-1, "名字不能与该版块下其他子版块名重复", null);
            }
        }
        LambdaQueryWrapper<SubSection> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(SubSection::getId, subsectionId);
        queryWrapper2.last("limit 1");
        SubSection subSection = subSectionMapper.selectOne(queryWrapper2);
        subSection.setName(name);
        subSectionMapper.updateById(subSection);

        return Result.success(20000, "okk", null);
    }

    @Override
    public Result changeSectionAvatar(Long userId, Long sectionId, String avatar) {
        Section section = this.findSectionById(sectionId);
        if (!Objects.equals(userId, section.getModeratorId())) {
            return Result.fail(-1, "没有权限", null);
        } else {
            section.setAvatar(avatar);
            sectionMapper.updateById(section);
            return Result.success(20000, "okk", null);
        }
    }


    private List<MySectionsVO> copyToMySectionsVO(List<Section> sections) {
        List<MySectionsVO> mySectionsVOList = new ArrayList<>();
        for (Section section :
                sections) {
            mySectionsVOList.add(copyToMySection(section));
        }
        return mySectionsVOList;
    }

    private MySectionsVO copyToMySection(Section section) {
        MySectionsVO mySectionsVO = new MySectionsVO();
        mySectionsVO.setSectionId(section.getId());
        mySectionsVO.setSectionAvatar(section.getAvatar());
        mySectionsVO.setSectionName(section.getName());
        mySectionsVO.setUserCounts(section.getUserCount());
        mySectionsVO.setPostCounts(section.getPostCount());
        mySectionsVO.setSectionIntro(section.getDescription());
        mySectionsVO.setSubSectionList(findSubSectionBySectionId(section.getId()));
        return mySectionsVO;
    }

    private List<ForumSectionVO> copyList(List<Section> sections) {
        List<ForumSectionVO> forumSectionVOS = new ArrayList<>();
        for (Section section :
                sections) {
            forumSectionVOS.add(copy(section));

        }
        return forumSectionVOS;
    }

    private ForumSectionVO copy(Section section) {
        ForumSectionVO forumSectionVO = new ForumSectionVO();
        forumSectionVO.setSectionId(section.getId());
        forumSectionVO.setName(section.getName());
        forumSectionVO.setSummary(section.getDescription());
        forumSectionVO.setAvatar(section.getAvatar());
        forumSectionVO.setCollectCount(section.getUserCount());
        return forumSectionVO;
    }

    @Override
    public Result getUserBySearch(Long userId, SearchUserDTO searchUserDTO) {
        String content = searchUserDTO.getContent();
        int page = searchUserDTO.getPage();
        int perPage = searchUserDTO.getPerPage();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            queryWrapper.like(User::getUsername, content);
        }
        queryWrapper.last("limit " + (page - 1) * perPage + "," + perPage);

        List<User> users = userMapper.selectList(queryWrapper);

        LambdaQueryWrapper<User> countWrapper = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            countWrapper.like(User::getUsername, content);
        }
        Integer total = Math.toIntExact(userMapper.selectCount(countWrapper));

        return Result.success(20000, "okk", new ForumUserDTO(total, usercopyList(users)));
        //return null;
    }

    private List<ForumUserVO> usercopyList(List<User> users) {
        List<ForumUserVO> forumUserVOS = new ArrayList<>();
        for (User user :
                users) {
            forumUserVOS.add(usercopy(user));

        }
        return forumUserVOS;
    }

    private ForumUserVO usercopy(User user) {
        ForumUserVO forumUserVO = new ForumUserVO();
        forumUserVO.setId(user.getId());
        forumUserVO.setUsername(user.getUsername());
        forumUserVO.setEmail(user.getEmail());
        forumUserVO.setGender(user.getGender());
        forumUserVO.setAvatar(user.getAvatar());
        forumUserVO.set_moderator(user.getIsModerator());
        return forumUserVO;
    }

    @Override
    public Result transferSection(Long userId, TransferSectionDTO transferSectionDTO) {
        Long targetId = transferSectionDTO.getTargetId();
        Long sectionId = transferSectionDTO.getSectionId();

        LambdaQueryWrapper<Moderator> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Moderator::getSectionId, sectionId);
        queryWrapper1.last("limit 1");
        Moderator moderator = moderatorMapper.selectOne(queryWrapper1);
        moderator.setUserId(targetId);
        moderatorMapper.updateById(moderator);

        LambdaQueryWrapper<Section> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Section::getId, sectionId);
        queryWrapper2.last("limit 1");
        Section section = sectionMapper.selectOne(queryWrapper2);
        section.setModeratorId(targetId);
        sectionMapper.updateById(section);

        LambdaQueryWrapper<User> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(User::getId, targetId);
        queryWrapper3.last("limit 1");
        User user = userMapper.selectOne(queryWrapper3);
        user.setIsModerator(true);
        userMapper.updateById(user);


        return Result.success(20000, "okk", null);
    }
}
