package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.*;
import com.techwave.entity.dto.PostDataDTO;
import com.techwave.entity.dto.PostPublishDTO;
import com.techwave.entity.vo.*;
import com.techwave.mapper.*;
import com.techwave.service.*;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CommentAndBodyMapper commentAndBodyMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ReplyMapper replyMapper;
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
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private BanService banService;

    @Override
    public List<PostDataVO> findPostBySectionIdWithPage(Long sectionId, int curPage, int limit) {
        Page<Post> page = new Page<>(curPage, limit);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, sectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        return copyList(postPage.getRecords());
    }

    @Override
    public List<PostDataVO> findPostBySectionIdAndSubSectionId(Long subsectionId, int curPage, int perPage) {
        Page<Post> page = new Page<>(curPage, perPage);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Post::getSubsectionId, subsectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        return copyList(postPage.getRecords());
    }


    @Override
    public Result getPostData(Long userId, PostDataDTO postDataDTO) throws ParseException {
        Long postId = postDataDTO.getId();
        Integer pageNo = postDataDTO.getPage();
        Integer pageSize = postDataDTO.getPerPage();
        Boolean isOnlyHost = postDataDTO.getIsOnlyHost();

        if (postId == null || pageNo == null || pageSize == null || isOnlyHost == null) {
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
        postVO.setSectionId(post.getSectionId());

        Section section = sectionService.findSectionById(post.getSectionId());
        postVO.setSectionName(section.getName());
        postVO.setTime(post.getUpdateTime());
        postVO.setContent(this.findBodyByPostId(postId));

        if (userId != null) {
            postVO.setIsCollected(collectService.isUserCollectPost(userId, postId));
            postVO.setIsLiked(likeMapper.selectIsUserLikePost(userId, postId));
            postVO.setIsBanned(banService.getUserIsBannedInSection(userId, section.getId()));
        } else {
            postVO.setIsCollected(false);
            postVO.setIsLiked(false);
            postVO.setIsBanned(false);
        }

        Post post1 = this.findPostById(postId);
        User user = userService.findUserById(post1.getAuthorId());
        postVO.setAuthor(user.getUsername());
        postVO.setAuthorId(user.getId());
        postVO.setAvatar(user.getAvatar());
        postVO.setBrowseNumber(post.getViewCount());
        postVO.setLikeCount(post.getLikeCount());

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
        postVO.setCommentVOList(commentService.findCommentVOsByPostIdWithPage(userId, postId, pageNo, pageSize, isOnlyHost, post1.getAuthorId())); //userId是发送请求的用户的id

        threadService.updateViewCount(postMapper, post); //通过线程池更新阅读数

        return Result.success(20000, "获取帖子数据成功", postVO);
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
        Section section = sectionService.findSectionById(postPublishDTO.getSectionId());
        SubSection subsection = sectionService.findSubSectionById(postPublishDTO.getSubsectionId());
        if(section==null || subsection==null ||postPublishDTO.getTitle()==null
                || Objects.equals(postPublishDTO.getTitle(), "") || Objects.equals(postPublishDTO.getContent(), "") || postPublishDTO.getContent()==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg());
        }
        post.setSectionId(postPublishDTO.getSectionId());
        post.setTitle(postPublishDTO.getTitle());
        post.setSubsectionId(postPublishDTO.getSubsectionId());
        post.setAuthorId(userId);
        post.setIsDeleted(false);
        post.setIsBanned(false);
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
        threadService.updatePostByCommentCount(postMapper, post, b);
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

    @Override
    public List<PostDataVO> findPostBySectionIdWithPageAndContent(Long sectionId, Integer page, Integer perPage, String content) {
        Page<Post> postPage = new Page<>(page, perPage);
        //匹配标题
        LambdaQueryWrapper<Post> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Post::getSectionId, sectionId);
        queryWrapper1.eq(Post::getIsDeleted, false);
        if (content != null && !content.equals("")) {
            queryWrapper1.like(Post::getTitle, content);
        }
        List<Post> posts1 = postMapper.selectList(queryWrapper1);
        //匹配帖子内容
        LambdaQueryWrapper<PostAndBody> queryWrapper2 = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            queryWrapper2.like(PostAndBody::getContent, content);
        }
        List<PostAndBody> postAndBodies = postAndBodyMapper.selectList(queryWrapper2);
        List<Post> posts2 = new ArrayList<>();
        for (PostAndBody postAndBody:
             postAndBodies) {
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Post::getId,postAndBody.getPostId());
            posts2.add(postMapper.selectOne(queryWrapper));
        }
        //匹配评论
        LambdaQueryWrapper<CommentAndBody> queryWrapper3 = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            queryWrapper3.like(CommentAndBody::getContent, content);
        }
        List<CommentAndBody> commentAndBodies = commentAndBodyMapper.selectList(queryWrapper3);
        List<Comment> comments = new ArrayList<>();
        for (CommentAndBody commentAndBody:
             commentAndBodies) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getId,commentAndBody.getCommentId());
            comments.add(commentMapper.selectOne(queryWrapper));
        }
        //匹配回复
        LambdaQueryWrapper<Reply> queryWrapper4 = new LambdaQueryWrapper<>();
        if (content != null && !content.equals("")) {
            queryWrapper4.like(Reply::getContent, content);
        }
        List<Reply> replies = replyMapper.selectList(queryWrapper4);
        for (Reply reply:
            replies) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getId,reply.getCommentId());
            comments.add(commentMapper.selectOne(queryWrapper));
        }
        List<Post> posts3 = new ArrayList<>();
        for (Comment comment:
                comments) {
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Post::getId,comment.getPostId());
            posts3.add(postMapper.selectOne(queryWrapper));
        }
        List<Post> posts = Stream.of(posts1, posts2, posts3)
                .flatMap(List::stream) // 1.将三个List合并为一个Stream
                .distinct() //2.去重
                .sorted(Comparator.comparing(Post::getUpdateTime).reversed()) // 3.按照createTime降序排序
                .collect(Collectors.toList()); // 4.转为List
        int totalSize = posts.size();
        int startIndex = (page - 1) * perPage;
        if (startIndex > totalSize) { // 如果起始索引大于数据总数，返回空List
            return Collections.emptyList();
        }
        int endIndex = Math.min(startIndex + perPage, totalSize);
        return copyList(posts.subList(startIndex, endIndex));
    }

    @Override
    public List<PostDataVO> findPinnedPostsBySectionId(Long sectionId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, sectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.eq(Post::getIsPinned, true);
        List<Post> postList = postMapper.selectList(queryWrapper);

        return copyList(postList);
    }

    @Override
    public List<PostDataVO> findHighlightedPostBySectionIdWithPage(Long sectionId, Integer page, Integer perPage) {
        Page<Post> postPage = new Page<>(page, perPage);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getSectionId, sectionId);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.eq(Post::getIsHighlighted, true);

        Page<Post> postPage1 = postMapper.selectPage(postPage, queryWrapper);
        return copyList(postPage1.getRecords());
    }

    @Override
    public Result likeOrUnlikePost(Long userId, Long postId) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getUserId, userId);
        queryWrapper.eq(Like::getPostId, postId);
        Like like = likeMapper.selectOne(queryWrapper);
        if (like == null) {
            like = new Like();
            like.setUserId(userId);
            like.setPostId(postId);
            like.setAuthorId(postMapper.selectById(postId).getAuthorId());
            likeMapper.insert(like);

            Notification notification = new Notification();
            notification.setSenderId(userId);
            notification.setUserId(postMapper.selectById(postId).getAuthorId());
            notification.setNotificationType("like");
            notification.setContent("用户" + userService.findUserById(userId).getUsername() + "点赞了你的帖子《" + postMapper.selectById(postId).getTitle() + "》");
            notification.setIsRead(false);
            notification.setLink("/post/" + postId);

            notificationMapper.insert(notification);

            Post post = postMapper.selectById(postId);
            post.setLikeCount(post.getLikeCount() + 1);
            postMapper.updateById(post);

            return Result.success(20000, "like successfully", null);
        } else {
            likeMapper.deleteById(like.getId());

            LambdaQueryWrapper<Notification> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Notification::getSenderId, userId);
            queryWrapper1.eq(Notification::getUserId, postMapper.selectById(postId).getAuthorId());
            queryWrapper1.eq(Notification::getNotificationType, "like");
            queryWrapper1.eq(Notification::getLink, "/post/" + postId);

            notificationMapper.delete(queryWrapper1);

            Post post = postMapper.selectById(postId);
            post.setLikeCount(post.getLikeCount() - 1);
            postMapper.updateById(post);

            return Result.success(20000, "unlike successfully", null);
        }
    }
    @Override
    public Result pinOrUnpinPost(Long userId, Long postId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        Post post = postMapper.selectOne(queryWrapper);
        if (post.getIsPinned() == false) {
            post.setIsPinned(true);
            postMapper.updateById(post);

            Notification notification = new Notification();
            notification.setUserId(postMapper.selectById(postId).getAuthorId());
            notification.setNotificationType("system");
            notification.setContent("版主置顶了你的帖子《" + postMapper.selectById(postId).getTitle() + "》");
            notification.setIsRead(false);
            notification.setLink("/post/" + postId);
            return Result.success(20000, "pin successfully", null);


        } else {
            post.setIsPinned(false);
            postMapper.updateById(post);

            LambdaQueryWrapper<Notification> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Notification::getUserId, postMapper.selectById(postId).getAuthorId());
            queryWrapper1.eq(Notification::getNotificationType, "system");
            queryWrapper1.eq(Notification::getLink, "/post/" + postId);

            notificationMapper.delete(queryWrapper1);

            return Result.success(20000, "unpin successfully", null);
        }
    }

    @Override
    public Result highlightOrUnhighlightPost(Long userId, Long postId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, postId);
        Post post = postMapper.selectOne(queryWrapper);
        if (post.getIsHighlighted() == false) {
            post.setIsHighlighted(true);
            postMapper.updateById(post);

            Notification notification = new Notification();
            notification.setUserId(postMapper.selectById(postId).getAuthorId());
            notification.setNotificationType("system");
            notification.setContent("版主将你的帖子《" + postMapper.selectById(postId).getTitle() + "》设为精华");
            notification.setIsRead(false);
            notification.setLink("/post/" + postId);
            return Result.success(20000, "highlight successfully", null);


        } else {
            post.setIsHighlighted(false);
            postMapper.updateById(post);

            LambdaQueryWrapper<Notification> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Notification::getUserId, postMapper.selectById(postId).getAuthorId());
            queryWrapper1.eq(Notification::getNotificationType, "system");
            queryWrapper1.eq(Notification::getLink, "/post/" + postId);

            notificationMapper.delete(queryWrapper1);

            return Result.success(20000, "unhighlight successfully", null);
        }
    }

    @Override
    public Result deletePostByModerator(Long userId, Long postId) {
        User user = userService.findUserById(userId);
        // judge if the user is the moderator of the section
        Boolean isModeratorOfSection = false;
        if (user.getIsModerator() != false) {
            Long thisSectionId = postMapper.selectById(postId).getSectionId();
            List<Long> sectionIds = sectionService.findSectionIdsByModeratorId(userId);
            for (Long sectionId :
                    sectionIds) {
                if (thisSectionId.equals(sectionId)) {
                    isModeratorOfSection = true;
                    break;
                }
            }
        }

        if (isModeratorOfSection) {
            Post post = postMapper.selectById(postId);
            post.setIsDeleted(true);
            postMapper.updateById(post);
            sectionService.updateSectionByPostCount(post.getSectionId(), false);
            return Result.success(20000, "delete successfully", null);
        } else {
            return Result.fail(-1, "no enough authentication", null);
        }
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
        return postMapper.selectOne(queryWrapper);
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
        //queryWrapper.eq(Post::getSectionId, 1);
        //queryWrapper.eq(Post::getIsHighlighted, true);
        queryWrapper.eq(Post::getIsDeleted, false);
        queryWrapper.like(Post::getTitle, "新闻");
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
        postDataVO.setAuthor(userService.findUserById(post.getAuthorId()).getUsername());
        postDataVO.setId(post.getId());
        postDataVO.setLikeCount(post.getLikeCount());
        postDataVO.setCommentCount(post.getCommentCount());
        postDataVO.setTime(post.getUpdateTime());
        postDataVO.setTitle(post.getTitle());
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
