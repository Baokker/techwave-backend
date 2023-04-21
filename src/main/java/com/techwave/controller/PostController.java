package com.techwave.controller;

import com.techwave.entity.dto.*;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.OssService;
import com.techwave.utils.Result;
import com.techwave.service.CollectService;
import com.techwave.service.CommentService;
import com.techwave.service.PostService;

import com.techwave.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("{{post_id}}")
    public Result getPostData(@RequestHeader(value = "JK-Token", required = false) String token, @PathVariable String post_id, Integer offset, Integer limit){
        PostDataDTO postDataDTO = new PostDataDTO(Long.valueOf(post_id),offset,limit);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return postService.getPostData(null,postDataDTO);
        }
        Long userId = Long.parseLong(userIdStr);
        return postService.getPostData(userId,postDataDTO);
    }

    @PostMapping("collect")
    public Result collectPost(@RequestHeader("T-Token") String token, @RequestBody CollectPostDTO collectPostDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return collectService.collectPost(userId,collectPostDTO);
    }

    @PostMapping("reply_on_post")
    public Result replyOnPost(@RequestHeader("T-Token") String token, @RequestBody ReplyOnPostDTO replyOnPostDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if(replyOnPostDTO.getPostId()==null||replyOnPostDTO.getContent()==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(),null);
        }
        return commentService.replyOnPost(userId,replyOnPostDTO);
    }

    @PostMapping("reply_on_comment")
    public Result replyOnComment(@RequestHeader("T-Token") String token, @RequestBody ReplyOnCommentDTO replyOnCommentDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if(replyOnCommentDTO.getCommentId()==null||replyOnCommentDTO.getContent()==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(),null);
        }
        return replyService.replyOnComment(userId,replyOnCommentDTO);
    }

    @PostMapping("reply_on_reply")
    public Result replyOnReply(@RequestHeader("T-Token") String token, @RequestBody ReplyOnReplyDTO replyOnReplyDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        if(replyOnReplyDTO.getReplyId()==null||replyOnReplyDTO.getContent()==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(),null);
        }
        return replyService.replyOnReply(userId,replyOnReplyDTO);
    }

    @DeleteMapping("{{comment_id}}")
    public Result deleteComment(@RequestHeader("T-Token") String token, @PathVariable Integer comment_id){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        Long commentId = Long.valueOf(comment_id);
        return commentService.deleteComment(userId,commentId);
    }

    @DeleteMapping("{{reply_id}}")
    public Result deleteReply(@RequestHeader("T-Token") String token, @PathVariable String reply_id){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        Long replyId = Long.valueOf(reply_id);
        return replyService.deleteReply(userId,replyId);
    }

    @PostMapping("report")
    public Result report(@RequestHeader("T-Token") String token, @RequestBody ReportDTO reportDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        return null;
    }

    @PostMapping("like")
    public Result likePost(@RequestHeader("T-Token") String token, @RequestBody String postId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        return null;
    }

    @PostMapping("follow_user")
    public Result followUser(@RequestHeader("T-Token") String token, @RequestBody String targetId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);

        return null;
    }

    @PostMapping("upload_picture")
    public Result uploadPicture(@RequestHeader("T-Token") String token,@RequestParam("image") MultipartFile picture){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        String s = ossService.uploadFile(picture);
        Map<String,String> map = new HashMap<>();
        map.put("url",s);

        return Result.success(20000,"okk",map);
    }

}
