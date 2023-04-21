package com.techwave.service;

import com.techwave.entity.dto.ReplyOnCommentDTO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.vo.ReplyVO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnReplyDTO;

import java.util.List;

public interface ReplyService {
    Result replyOnComment(Long userId, ReplyOnCommentDTO replyOnCommentDTO);

    Result replyOnReply(Long userId, ReplyOnReplyDTO replyOnReplyDTO);

    Result deleteReply(Long userId, Long replyId);

    List<ReplyVO> findRepliesByCommentId(Long id, Long userId);

    List<MyReplyVO> findRepliesByUserId(Long userId);
}
