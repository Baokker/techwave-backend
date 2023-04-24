package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.*;
import com.techwave.entity.dto.PostPublishDTO;
import com.techwave.entity.vo.*;
import com.techwave.service.*;
import com.techwave.utils.TCode;
import com.techwave.utils.Result;
import com.techwave.entity.dto.PostDataDTO;
import com.techwave.mapper.PostAndBodyMapper;
import com.techwave.mapper.PostMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * post服务类的实现类
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 16:17
 * @since JDK18
 */
@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostAndBodyMapper postAndBodyMapper;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private UserService userService;
    @Autowired
    private CollectService collectService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CommentService commentService;


    @Override
    public List<PostDataVO> findPostBySectionIdWithPage(Long sectionId, int curPage, int limit) {
        Page<Post> page = new Page<>(curPage, limit);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, sectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        List<PostDataVO> postDataVOList = copyList(postPage.getRecords());
        return postDataVOList;
    }

    @Override
    public List<PostDataVO> findPostBySectionIdAndSubSectionId(Long sectionId, Long subsectionId, int curPage, int limit) {
        Page<Post> page = new Page<>(curPage, limit);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, sectionId);
        queryWrapper.in(Post::getSubsectionId, subsectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        List<PostDataVO> postDataVOList = copyList(postPage.getRecords());
        return postDataVOList;
    }


    @Override
    public Result getPostData(Long userId, PostDataDTO postDataDTO) {
        Long postId = postDataDTO.getId();
        Integer pageNo = postDataDTO.getPage();
        Integer pageSize = postDataDTO.getPerPage();

        if (postId == null || pageNo == null || pageSize == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg());
        }

        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.last("limit 1");
        Post post = postMapper.selectOne(queryWrapper);

        if (post == null) {
            return Result.fail(-1, "该帖子不存在", null);
        }

        PostVO postVO = new PostVO();

        postVO.setTitle(post.getTitle());
        if (userId != null) {
            postVO.setIsCollected(collectService.isUserCollectPost(userId, postId));
        } else {
            postVO.setIsCollected(false);
        }

        postVO.setSectionId(post.getSectionId());

        Section section = sectionService.findSectionById(post.getSectionId());
        postVO.setSectionName(section.getName());
        postVO.setTime(post.getUpdateTime());
        postVO.setContent(this.findBodyByPostId(postId));

        Post post1 = this.findPostById(postId);
        User user = userService.findUserById(post1.getAuthorId());
        postVO.setAuthor(user.getUsername());
        postVO.setAvatar(user.getAvatar());
        postVO.setBrowseNumber(post.getViewCount());

        if (post.getSubsectionId() == null) {
            postVO.setSubsectionId(null);
            postVO.setSubsectionName(null);
        } else {
            postVO.setSubsectionId(post.getSubsectionId());
            SubSection subSection = sectionService.findSubSectionById(post.getSubsectionId());
            if (subSection == null) {
                postVO.setSubsectionName(null);
            } else {
                postVO.setSubsectionName(subSection.getName());
            }
        }

        postVO.setTotal(post.getCommentCount() + 1);
        postVO.setCommentVOList(commentService.findCommentVOsByPostIdWithPage(userId, postId, pageNo, pageSize)); //userId是发送请求的用户的id

        threadService.updateViewCount(postMapper, post); //通过线程池更新阅读数

        return Result.success(20000, postVO);
    }

    private String findBodyByPostId(Long postId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Post post = postMapper.selectOne(queryWrapper);
        LambdaQueryWrapper<PostAndBody> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PostAndBody::getId, post.getBodyId());
        PostAndBody postAndBody = postAndBodyMapper.selectOne(queryWrapper1);
        return postAndBody.getContent();
    }


    @Override
    public Result publishPost(Long userId, PostPublishDTO postPublishDTO) {
        Post post = new Post();
        post.setSectionId(postPublishDTO.getSectionId());
        post.setTitle(postPublishDTO.getTitle());
        post.setSubsectionId(postPublishDTO.getSubsectionId());
        post.setAuthorId(userId);
        post.setIsDeleted(false);
        post.setUpdateTime(LocalDateTime.now());
        this.postMapper.insert(post);

        PostAndBody postAndBody = new PostAndBody();
        postAndBody.setPostId(post.getId());
        postAndBody.setContent(postPublishDTO.getContent());
        postAndBodyMapper.insert(postAndBody);

        post.setBodyId(postAndBody.getId());
        postMapper.updateById(post);

        sectionService.updateSectionByPostCount(postPublishDTO.getSectionId(), true);
        return Result.success(20000, "操作成功", null);
    }

    @Override
    public void updatePostByCommentCount(Long postId, boolean b) {
        Post post = this.findPostById(postId);
        threadService.updatePostByCommentCount(postMapper, post, true);
    }

    @Override
    public List<FolderPostVO> findPostsByFolderIdWithPage(Long folderId, Integer curPage, Integer limit) {
        List<Long> postIds = collectService.findPostIdsByFolderId(folderId);
        if (postIds == null || postIds.size() == 0) {
            return null;
        }
        Page<Post> postPage = new Page<>(curPage, limit);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Post::getId, postIds);
        queryWrapper.eq(Post::getIsDeleted, false);
        Page<Post> postPage1 = postMapper.selectPage(postPage, queryWrapper);
        List<Post> records = postPage1.getRecords();
        return copyListFolder(records);
    }

    @Override
    public Result findPostsByUserIdWithPage(Long userId, String type, Integer curPage, Integer limit) {
        Page<Post> postPage = new Page<>(curPage, limit);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.eq(Post::getAuthorId, userId);
        Page<Post> postPage1 = postMapper.selectPage(postPage, queryWrapper);
        List<Post> postList = postPage1.getRecords();
        MyPostVO myPostVO = new MyPostVO();
        myPostVO.setTotal(postList.size());
        myPostVO.setMyPosts(copyToMyPosts(postList));
        return Result.success(20000, "okk", myPostVO);
    }

    @Override
    public Result deleteMyPost(Long postId, Long userId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        queryWrapper.eq(Post::getAuthorId, userId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Post post = postMapper.selectOne(queryWrapper);
        if (post == null) {
            return Result.fail(-1, "参数有误", null);
        } else {
            post.setIsDeleted(true);
            postMapper.updateById(post);
            sectionService.updateSectionByPostCount(post.getSectionId(), false);
            return Result.success(20000, "okk", null);
        }
    }

    @Override
    public List<Long> findPostIdsByUserId(Long userId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getAuthorId, userId);
        queryWrapper.eq(Post::getIsDeleted, false);
        List<Post> postList = postMapper.selectList(queryWrapper);
        List<Long> postIds = new ArrayList<>();
        for (Post post :
                postList) {
            postIds.add(post.getId());
        }
        return postIds;
    }

    private List<MyPostContentVO> copyToMyPosts(List<Post> postList) {
        List<MyPostContentVO> myPostContentVOS = new ArrayList<>();
        for (Post post :
                postList) {
            myPostContentVOS.add(copyToMyPost(post));
        }
        return myPostContentVOS;
    }

    private MyPostContentVO copyToMyPost(Post post) {
        MyPostContentVO myPostContentVO = new MyPostContentVO();
        myPostContentVO.setPostId(post.getId());
        myPostContentVO.setTitle(post.getTitle());
        myPostContentVO.setUpdateTime(post.getUpdateTime().toString());
        myPostContentVO.setSectionName(sectionService.findSectionById(post.getSectionId()).getName());
        myPostContentVO.setCommentCount(post.getCommentCount());
        return myPostContentVO;
    }

    private Post findPostById(Long postId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.last("limit 1");
        Post post = postMapper.selectOne(queryWrapper);
        return post;
    }

    @Override
    public Result hotPost() {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Post::getCommentCount);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.last("limit 10");
        List<Post> postList = postMapper.selectList(queryWrapper);
        return Result.success(copyList(postList));
    }

    @Override
    public Result getNews() {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, 1);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.orderByDesc(Post::getCommentCount);
        queryWrapper.last("limit 10");
        List<Post> postList = postMapper.selectList(queryWrapper);
        return Result.success(copyList(postList));
    }


    private List<PostDataVO> copyList(List<Post> postList) {
        List<PostDataVO> voList = new ArrayList<>();
        for (Post post : postList) {
            voList.add(copy(post));
        }
        return voList;
    }

    private List<FolderPostVO> copyListFolder(List<Post> postList) {
        List<FolderPostVO> voList = new ArrayList<>();
        for (Post post : postList) {
            voList.add(copyFolder(post));
        }
        return voList;
    }

    private PostDataVO copy(Post post) {
        PostDataVO postDataVO = new PostDataVO();
        BeanUtils.copyProperties(post, postDataVO);
        postDataVO.setPoster(userService.findUserById(post.getAuthorId()).getUsername());
        return postDataVO;
    }

    private FolderPostVO copyFolder(Post post) {
        FolderPostVO folderPostVO = new FolderPostVO();
        folderPostVO.setTime(post.getUpdateTime());
        folderPostVO.setPostId(post.getId());
        folderPostVO.setTitle(post.getTitle());
        Section sectionById = sectionService.findSectionById(post.getSectionId());
        folderPostVO.setSectionName(sectionById.getName());
        folderPostVO.setPosterName(userService.findUserById(post.getAuthorId()).getUsername());
        return folderPostVO;
    }
}
