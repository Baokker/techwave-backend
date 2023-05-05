package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.Comment;
import com.techwave.entity.CommentAndBody;
import com.techwave.entity.PostAndComment;
import com.techwave.entity.User;
import com.techwave.entity.vo.CommentVO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.vo.ReplyVO;
import com.techwave.mapper.UserMapper;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnPostDTO;
import com.techwave.mapper.CommentAndBodyMapper;
import com.techwave.mapper.CommentMapper;
import com.techwave.mapper.PostAndCommentMapper;
import com.techwave.service.CommentService;
import com.techwave.service.PostService;
import com.techwave.service.ReplyService;
import com.techwave.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        List<CommentVO> commentVOList = this.copyList(records, userId);
        return commentVOList;
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
        myReplyVO.setTime(comment.getCreatedAt());
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
        commentVO.setTime(comment.getCreatedAt());
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
