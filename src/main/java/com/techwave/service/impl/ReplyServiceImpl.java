package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.CommentAndReply;
import com.techwave.entity.Reply;
import com.techwave.entity.dto.ReplyOnCommentDTO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.vo.ReplyVO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnReplyDTO;
import com.techwave.mapper.CommentAndReplyMapper;
import com.techwave.mapper.ReplyMapper;
import com.techwave.service.ReplyService;
import com.techwave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        List<ReplyVO> replyVOList = copyReplies(replies, userId);
        return replyVOList;
    }

    @Override
    public List<MyReplyVO> findRepliesByUserId(Long userId) {
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reply::getToId, userId);
        List<Reply> replies = replyMapper.selectList(queryWrapper);
        List<MyReplyVO> myReplyVOList = copyMyReplies(replies);
        return myReplyVOList;
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
        MyReplyVO myReplyVO = new MyReplyVO();
        myReplyVO.setTime(reply.getCreatedAt());
        myReplyVO.setId(reply.getId());
        myReplyVO.setType("Reply");
        myReplyVO.setContent(reply.getContent());
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
