package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.*;
import com.techwave.entity.vo.CommentVO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.vo.ReplyVO;
import com.techwave.mapper.*;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnPostDTO;
import com.techwave.service.CommentService;
import com.techwave.service.PostService;
import com.techwave.service.ReplyService;
import com.techwave.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 评论服务类的实现类
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/5 15:59
 * @since JDK18
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentAndBodyMapper commentAndBodyMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private PostAndCommentMapper postAndCommentMapper;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private PostMapper postMapper;

    @Override
    public List<CommentVO> findCommentVOsByPostIdWithPage(Long userId, Long postId, Integer offset, Integer limit, Boolean isOnlyHost, Long authorId) {
        Page<Comment> commentPage = new Page<>(offset, limit);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId);
        if (isOnlyHost) {
            queryWrapper.eq(Comment::getAuthorId, authorId);
        }
        Page<Comment> commentPage1 = commentMapper.selectPage(commentPage, queryWrapper);
        List<Comment> records = commentPage1.getRecords();
        return this.copyList(records, userId);
    }

    @Override
    public Result replyOnPost(Long userId, ReplyOnPostDTO replyOnPostDTO) {
        Long postId = replyOnPostDTO.getPostId();

        postService.updatePostByCommentCount(postId, true);

        String content = replyOnPostDTO.getContent();
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(userId);
        comment.setCreatedAt(LocalDateTime.now());

        commentMapper.insert(comment);

        CommentAndBody commentAndBody = new CommentAndBody();
        commentAndBody.setCommentId(comment.getId());
        commentAndBody.setContent(content);

        commentAndBodyMapper.insert(commentAndBody);

        comment.setBodyId(commentAndBody.getId());
        commentMapper.updateById(comment);

        PostAndComment postAndComment = new PostAndComment();
        postAndComment.setPostId(postId);
        postAndComment.setCommentId(comment.getId());
        postAndCommentMapper.insert(postAndComment);

        if (!Objects.equals(userId, postMapper.selectById(postId).getAuthorId())) {
            // 发送通知
            Notification notification = new Notification();
            notification.setSenderId(userId);
            notification.setUserId(postMapper.selectById(postId).getAuthorId());
            notification.setTitle(userService.findUserById(userId).getUsername() + "评论了你的帖子《" + postMapper.selectById(postId).getTitle() + "》");
            notification.setLink("/post/" + postId);
            notification.setNotificationType("reply");

            Document doc = Jsoup.parse(replyOnPostDTO.getContent());
            String text = doc.text().replaceAll("<.*?>", ""); // 提取纯文本并过滤 HTML 标签及其属性
            notification.setContent(text);

            notification.setIsRead(false);

            notificationMapper.insert(notification);
        }

        return Result.success(20000, "操作成功", null);
    }

    @Override
    public Result deleteComment(Long userId, Long commentId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getId, commentId);
        queryWrapper.eq(Comment::getAuthorId, userId);
        queryWrapper.last("limit 1");
        Comment comment = commentMapper.selectOne(queryWrapper);
        if (comment == null) {
            return Result.fail(-1, "参数有误", null);
        }
        commentMapper.delete(queryWrapper);

        LambdaQueryWrapper<PostAndComment> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(PostAndComment::getCommentId, commentId);
        queryWrapper2.last("limit 1");
        postAndCommentMapper.delete(queryWrapper2);

        postService.updatePostByCommentCount(comment.getPostId(), false);
        return Result.success(20000, "删除成功", null);
    }

    @Override
    public List<MyReplyVO> findCommentsByUserId(Long userId) {
        List<Long> postIds = postService.findPostIdsByUserId(userId);
        if (postIds.size() == 0) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Comment::getPostId, postIds);
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        return copyMyComments(commentList);
    }

    private List<MyReplyVO> copyMyComments(List<Comment> commentList) {
        List<MyReplyVO> myReplyVOList = new ArrayList<>();
        for (Comment comment :
                commentList) {
            myReplyVOList.add(copyMyComment(comment));
        }
        return myReplyVOList;
    }

    private MyReplyVO copyMyComment(Comment comment) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, comment.getAuthorId());
        MyReplyVO myReplyVO = new MyReplyVO();
        myReplyVO.setName(userMapper.selectOne(queryWrapper).getUsername());
        myReplyVO.setAvatar(userMapper.selectOne(queryWrapper).getAvatar());
        myReplyVO.setId(comment.getId());
        myReplyVO.setPostId(comment.getPostId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 自定义时间格式
        String filteredDateTime = comment.getCreatedAt().format(formatter); // 格式化时间
        myReplyVO.setTime(filteredDateTime);
        myReplyVO.setType("回复了我的帖子");
        Document doc = Jsoup.parse(this.findContentByBodyId(comment.getBodyId()));
        String text = doc.text().replaceAll("<.*?>", ""); // 提取纯文本并过滤 HTML 标签及其属性
        myReplyVO.setContent(text);
        return myReplyVO;
    }

    private List<CommentVO> copyList(List<Comment> commentList, Long userId) {
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment comment :
                commentList) {
            commentVOList.add(copy(comment, userId));
        }
        return commentVOList;
    }

    private CommentVO copy(Comment comment, Long userId) {
        CommentVO commentVO = new CommentVO();
        User user = userService.findUserById(comment.getAuthorId());
        commentVO.setCommentId(comment.getId());
        commentVO.setAuthorName(user.getUsername());
        commentVO.setAuthorId(user.getId());
        commentVO.setAvatar(user.getAvatar());
        commentVO.setTime(String.valueOf(comment.getCreatedAt()));
        commentVO.setContent(this.findContentByBodyId(comment.getBodyId()));
        commentVO.setAbleToDelete(Objects.equals(comment.getAuthorId(), userId));
        List<ReplyVO> replyVOList;
        replyVOList = replyService.findRepliesByCommentId(comment.getId(), userId);
        commentVO.setReplyVOList(replyVOList);
        return commentVO;
    }

    private String findContentByBodyId(Long bodyId) {
        LambdaQueryWrapper<CommentAndBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentAndBody::getId, bodyId);
        queryWrapper.last("limit 1");
        return commentAndBodyMapper.selectOne(queryWrapper).getContent();
    }
}
