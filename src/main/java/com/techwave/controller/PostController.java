package com.techwave.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.techwave.entity.dto.*;
import com.techwave.service.*;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.OssService;
import com.techwave.utils.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * post的控制类
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 20:08
 * @since JDK18
 */
@RestController
@RequestMapping("post/")
public class PostController {
    @Autowired
    private OssService ossService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private CollectService collectService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private FollowService followService;

    @GetMapping("{postId}")
    public Result getPostData(@RequestHeader(value = "T-Token", required = false) String token, @PathVariable String postId, @RequestParam Integer page, @RequestParam Integer perPage, @RequestParam Integer isOnlyHost) throws ParseException {
        Boolean isOnlyHostBoolean = isOnlyHost == 1;
        PostDataDTO postDataDTO = new PostDataDTO(Long.valueOf(postId), page, perPage, isOnlyHostBoolean);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return postService.getPostData(null, postDataDTO);
        }
        Long userId = Long.parseLong(userIdStr);
        return postService.getPostData(userId, postDataDTO);
    }

    @PostMapping("collect")
    public Result collectPost(@RequestHeader("T-Token") String token, @RequestBody CollectPostDTO collectPostDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return collectService.collectPost(userId, collectPostDTO);
    }

    @PostMapping("reply_on_post")
    public Result replyOnPost(@RequestHeader("T-Token") String token, @RequestBody ReplyOnPostDTO replyOnPostDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if (replyOnPostDTO.getPostId() == null || replyOnPostDTO.getContent() == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        }
        return commentService.replyOnPost(userId, replyOnPostDTO);
    }

    @PostMapping("reply_on_comment")
    public Result replyOnComment(@RequestHeader("T-Token") String token, @RequestBody ReplyOnCommentDTO replyOnCommentDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if (replyOnCommentDTO.getCommentId() == null || replyOnCommentDTO.getContent() == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        }
        return replyService.replyOnComment(userId, replyOnCommentDTO);
    }

    @PostMapping("reply_on_reply")
    public Result replyOnReply(@RequestHeader("T-Token") String token, @RequestBody ReplyOnReplyDTO replyOnReplyDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if (replyOnReplyDTO.getReplyId() == null || replyOnReplyDTO.getContent() == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        }
        return replyService.replyOnReply(userId, replyOnReplyDTO);
    }

    @DeleteMapping("comment/{commentId}")
    public Result deleteComment(@RequestHeader("T-Token") String token, @PathVariable Integer commentId) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        return commentService.deleteComment(Long.parseLong(userIdStr), Long.valueOf(commentId));
    }

    @DeleteMapping("reply/{replyId}")
    public Result deleteReply(@RequestHeader("T-Token") String token, @PathVariable String replyId) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        return replyService.deleteReply(Long.parseLong(userIdStr), Long.valueOf(replyId));
    }

    @PostMapping("report")
    public Result report(@RequestHeader("T-Token") String token, @RequestBody ModeratorReportDTO moderatorReportDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        return reportService.createModeratorReport(moderatorReportDTO, userId);
    }

    @PostMapping("like")
    public Result likePost(@RequestHeader("T-Token") String token, @RequestBody JSONObject jsonObject)  throws JSONException {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        Long postId = jsonObject.getLong("postId");

        return postService.likeOrUnlikePost(userId, postId);
    }

    @PostMapping("follow_user")
    public Result followUser(@RequestHeader("T-Token") String token, @RequestBody JSONObject jsonObject)  throws JSONException {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        Long followingId = jsonObject.getLong("followingId");

        return followService.followOrUnfollow(userId, followingId);
    }

    @PostMapping("upload_picture")
    public Result uploadPicture(@RequestHeader("T-Token") String token, @RequestParam("image") MultipartFile picture) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        String s = ossService.uploadFile(picture);
        Map<String, String> map = new HashMap<>();
        map.put("url", s);

        return Result.success(20000, "okk", map);
    }

}
