package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.*;
import com.techwave.entity.dto.ReplyOnCommentDTO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.vo.ReplyVO;
import com.techwave.mapper.*;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnReplyDTO;
import com.techwave.service.ReplyService;
import com.techwave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/6 16:02
 * @since JDK18
 */
@Service
public class ReplyServiceImpl implements ReplyService {
    @Autowired
    private UserService userService;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private CommentAndReplyMapper commentAndReplyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private CommentAndBodyMapper commentAndBodyMapper;

    @Override
    public Result replyOnComment(Long userId, ReplyOnCommentDTO replyOnCommentDTO) {
        Long commentId = replyOnCommentDTO.getCommentId();
        String content = replyOnCommentDTO.getContent();
        Reply reply = new Reply();
        reply.setCommentId(commentId);
        reply.setContent(content);
        reply.setAuthorId(userId);
        reply.setToId(userService.findUserIdByCommentId(commentId));
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);

        CommentAndReply commentAndReply = new CommentAndReply();
        commentAndReply.setReplyId(reply.getId());
        commentAndReply.setCommentId(commentId);
        commentAndReplyMapper.insert(commentAndReply);

        if (!Objects.equals(userId, userService.findUserIdByCommentId(commentId))) {
            Notification notification = new Notification();
            notification.setSenderId(userId);
            notification.setUserId(userService.findUserIdByCommentId(commentId));
            notification.setTitle(userService.findUserById(userId).getUsername() + "回复了你的评论:" + commentAndBodyMapper.selectById(commentId).getContent());
            notification.setLink("/post/" + commentMapper.selectById(commentId).getPostId());
            notification.setNotificationType("reply");

            Document doc = Jsoup.parse(replyOnCommentDTO.getContent());
            String text = doc.text().replaceAll("<.*?>", ""); // 提取纯文本并过滤 HTML 标签及其属性
            notification.setContent(text);

            notification.setIsRead(false);

            notificationMapper.insert(notification);
        }

        return Result.success(20000, "操作成功", null);
    }

    @Override
    public Result replyOnReply(Long userId, ReplyOnReplyDTO replyOnReplyDTO) {
        Long replyId = replyOnReplyDTO.getReplyId();
        String content = replyOnReplyDTO.getContent();

        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getId, replyId);
        queryWrapper.last("limit 1");
        Reply reply1 = replyMapper.selectOne(queryWrapper);
        if (reply1 == null) {
            return Result.fail(-1, "要回复的回复不存在", null);
        }

        Reply reply = new Reply();
        reply.setCommentId(reply1.getCommentId());
        reply.setContent(content);
        reply.setAuthorId(userId);
        reply.setToId(this.findUserIdByReplyId(replyId));
        reply.setCreatedAt(LocalDateTime.now());
        replyMapper.insert(reply);

        if (Objects.equals(userId, this.findUserIdByReplyId(replyId))) {
            return Result.success(20000, "操作成功", null);
        }

        Notification notification = new Notification();
        notification.setSenderId(userId);
        notification.setUserId(this.findUserIdByReplyId(replyId));
        notification.setTitle(userService.findUserById(userId).getUsername() + "回复了你的评论:" + reply1.getContent());

        LambdaQueryWrapper<Comment> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Comment::getId, reply.getCommentId());
        Comment temp = commentMapper.selectOne(queryWrapper2);
        if (temp == null) {
            notification.setLink("/post/0"); // 0代表评论不存在
        } else {
            notification.setLink("/post/" + temp.getPostId());
        }

        notification.setNotificationType("reply");
        Document doc = Jsoup.parse(replyOnReplyDTO.getContent());
        String text = doc.text().replaceAll("<.*?>", ""); // 提取纯文本并过滤 HTML 标签及其属性
        notification.setContent(text);

        notification.setIsRead(false);

        notificationMapper.insert(notification);

        return Result.success(20000, "操作成功", null);
    }

    @Override
    public Result deleteReply(Long userId, Long replyId) {
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getId, replyId);
        queryWrapper.last("limit 1");
        Reply reply = replyMapper.selectOne(queryWrapper);
        if (reply == null) {
            return Result.fail(-1, "参数有误", null);
        }
        replyMapper.delete(queryWrapper);

        LambdaQueryWrapper<CommentAndReply> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(CommentAndReply::getReplyId, replyId);
        queryWrapper1.last("limit 1");
        commentAndReplyMapper.delete(queryWrapper1);

        return Result.success(20000, "操作成功", null);
    }

    @Override
    public List<ReplyVO> findRepliesByCommentId(Long id, Long userId) {
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getCommentId, id);
        List<Reply> replies = replyMapper.selectList(queryWrapper);
        return copyReplies(replies, userId);
    }

    @Override
    public List<MyReplyVO> findRepliesByUserId(Long userId) {
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getToId, userId);
        List<Reply> replies = replyMapper.selectList(queryWrapper);
        return copyMyReplies(replies);
    }

    private List<MyReplyVO> copyMyReplies(List<Reply> replies) {
        List<MyReplyVO> myReplyVOList = new ArrayList<>();
        for (Reply reply :
                replies) {
            myReplyVOList.add(copyMyReply(reply));
        }
        return myReplyVOList;
    }

    private MyReplyVO copyMyReply(Reply reply) {
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getId, reply.getAuthorId());
        MyReplyVO myReplyVO = new MyReplyVO();
        myReplyVO.setName(userMapper.selectOne(queryWrapper1).getUsername());
        myReplyVO.setAvatar(userMapper.selectOne(queryWrapper1).getAvatar());
        LambdaQueryWrapper<Comment> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Comment::getId, reply.getCommentId());
        Comment temp = commentMapper.selectOne(queryWrapper2);
        if (temp == null)
            myReplyVO.setPostId(0L);
        else
            myReplyVO.setPostId(temp.getPostId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 自定义时间格式
        String filteredDateTime = reply.getCreatedAt().format(formatter); // 格式化时间
        myReplyVO.setTime(filteredDateTime);
        myReplyVO.setId(reply.getId());
        myReplyVO.setType("回复了我的评论");
        Document doc = Jsoup.parse(reply.getContent());
        String text = doc.text().replaceAll("<.*?>", ""); // 提取纯文本并过滤 HTML 标签及其属性
        myReplyVO.setContent(text);
        return myReplyVO;
    }

    private List<ReplyVO> copyReplies(List<Reply> replies, Long userId) {
        List<ReplyVO> replyVOList = new ArrayList<>();
        for (Reply reply :
                replies) {
            replyVOList.add(copy(reply, userId));
        }
        return replyVOList;
    }

    private ReplyVO copy(Reply reply, Long userId) {
        ReplyVO replyVO = new ReplyVO();
        replyVO.setReplyId(reply.getId());
        replyVO.setTime(reply.getCreatedAt());
        replyVO.setToName(userService.findUserById(reply.getToId()).getUsername());
        replyVO.setToId(reply.getToId());
        replyVO.setContent(reply.getContent());
        replyVO.setAuthorId(reply.getAuthorId());
        replyVO.setAuthorName(userService.findUserById(reply.getAuthorId()).getUsername());
        replyVO.setAbleToDelete(Objects.equals(userId, reply.getAuthorId()));
        return replyVO;
    }

    private Long findUserIdByReplyId(Long replyId) {
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getId, replyId);
        queryWrapper.last("limit 1");
        Reply reply = replyMapper.selectOne(queryWrapper);
        return reply.getAuthorId();
    }
}
