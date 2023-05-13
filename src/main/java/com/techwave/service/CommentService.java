package com.techwave.service;

import com.techwave.entity.CommentAndBody;
import com.techwave.entity.vo.CommentVO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.ReplyOnPostDTO;

import java.util.List;

public interface CommentService {

    List<CommentVO> findCommentVOsByPostIdWithPage(Long userId, Long postId, Integer offset, Integer limit, Boolean isOnlyHost, Long authorId);

    CommentAndBody findContentById(Long commentId);

    Result replyOnPost(Long userId, ReplyOnPostDTO replyOnPostDTO);

    Result deleteComment(Long userId, Long commentId);

    List<MyReplyVO> findCommentsByUserId(Long userId);
}
