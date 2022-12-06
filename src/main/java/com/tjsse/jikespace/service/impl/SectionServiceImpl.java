package com.tjsse.jikespace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tjsse.jikespace.entity.Section;
import com.tjsse.jikespace.entity.SubSection;
import com.tjsse.jikespace.entity.dto.PostsWithTagDTO;
import com.tjsse.jikespace.entity.dto.SectionDataDTO;
import com.tjsse.jikespace.entity.vo.PostDataVO;
import com.tjsse.jikespace.entity.vo.SectionDataVO;
import com.tjsse.jikespace.mapper.SectionMapper;
import com.tjsse.jikespace.mapper.SubSectionMapper;
import com.tjsse.jikespace.service.*;
import com.tjsse.jikespace.utils.Result;
import com.tjsse.jikespace.utils.JKCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private CollectService collectService;
    @Autowired
    private PostService postService;
    @Override
    public Result getSectionData(SectionDataDTO sectionDataDTO) {
        Long sectionId = sectionDataDTO.getSectionId();
        Long userId = sectionDataDTO.getUserId();
        Integer curPage = sectionDataDTO.getCurPage();
        Integer limit = sectionDataDTO.getLimit();
        if(sectionId==null||userId==null||curPage==null||limit==null)
            return Result.fail(JKCode.PARAMS_ERROR.getCode(), JKCode.PARAMS_ERROR.getMsg(),null);
        Section section = this.findSectionById(sectionId);
        if(section==null){
            return Result.fail(-1,"该版块不存在，参数有误",null);
        }
        SectionDataVO sectionDataVO = new SectionDataVO();
        sectionDataVO.setSectionName(section.getSectionName());
        sectionDataVO.setPostCounts(section.getPostCounts());
        sectionDataVO.setIsCollected(collectService.isUserCollectSection(userId,sectionId));
        sectionDataVO.setSectionSummary(section.getSectionSummary());
        sectionDataVO.setSubSectionList(this.findSubSectionBySectionId(sectionId));
        sectionDataVO.setPostVOList(postService.findPostBySectionIdWithPage(sectionId,curPage,limit));

        return Result.success(sectionDataVO);
    }

    @Override
    public Section findSectionById(Long sectionId) {
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getId,sectionId);
        queryWrapper.last("limit 1");
        return this.sectionMapper.selectOne(queryWrapper);
    }

    @Override
    public SubSection findSubSectionById(Long subsectionId) {
        LambdaQueryWrapper<SubSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubSection::getId,subsectionId);
        queryWrapper.last("limit 1");
        return this.subSectionMapper.selectOne(queryWrapper);
    }

    @Override
    public Result getPostsByTag(PostsWithTagDTO postsWithTagDTO) {
        Long sectionId = postsWithTagDTO.getSectionId();
        Long subsectionId = postsWithTagDTO.getSubsectionId();
        Integer curPage = postsWithTagDTO.getCurPage();
        Integer limit = postsWithTagDTO.getLimit();

        if(sectionId==null||subsectionId==null||curPage==null||limit==null)
            return Result.fail(JKCode.PARAMS_ERROR.getCode(), JKCode.PARAMS_ERROR.getMsg(),null);
        Section section = this.findSectionById(sectionId);
        if(section==null){
            return Result.fail(-1,"该版块不存在，参数有误",null);
        }

        List<PostDataVO> postList = postService.findPostBySectionIdAndSubSectionId(sectionId, subsectionId, curPage, limit);
        if(postList.size()==0)
            return Result.fail(-1,"没有帖子含该标签",null);
        return Result.success(postList);
    }

    @Override
    public List<SubSection> findSubSectionBySectionId(Long sectionId) {
        List<SubSection> subSectionList = subSectionMapper.findSubSectionBySectionId(sectionId);
        return subSectionList;
    }
}
