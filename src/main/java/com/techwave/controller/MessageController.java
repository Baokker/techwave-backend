package com.techwave.controller;


import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.dto.SendMessageDTO;
import com.techwave.service.CommentService;
import com.techwave.service.ReplyService;
import com.techwave.utils.JKCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("message/")
public class MessageController {

    @Autowired
    private ReplyService replyService;
    @Autowired
    private CommentService commentService;

    @GetMapping("reply")
    public Result getReply(@RequestHeader("JK-Token") String jk_token,Integer offset,Integer limit){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        List<MyReplyVO> myReplyVOList1 = replyService.findRepliesByUserId(userId);
        List<MyReplyVO> myReplyVOList2 = commentService.findCommentsByUserId(userId);

        myReplyVOList1.addAll(myReplyVOList2);
        myReplyVOList1.sort((t1,t2)->t2.getTime().compareTo(t1.getTime()));

        Set<MyReplyVO> collect = myReplyVOList1.stream()
                .skip((offset - 1) * limit)
                .limit(limit)
                .collect(Collectors.toSet());
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",collect.size());
        map.put("myReply",collect);
        return Result.success(20000,"okk",map);
    }


    @GetMapping("list")
    public Result getListData(@RequestHeader(value = "JK-Token", required = false) String jk_token){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("history")
    public Result getHistoryData(@RequestHeader(value = "JK-Token", required = false) String jk_token, Integer targetId){
        //只有targetId就不封装了
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("history")
    public Result deleteHistoryData(@RequestHeader(value = "JK-Token", required = false) String jk_token, Integer targetId){
        //只有targetId就不封装了
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("block")
    public Result blockUser(@RequestHeader("JK-Token") String jk_token, @RequestBody Map<String, String> map){
        Long targetId = Long.valueOf(map.get("targetId"));
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("send_message")
    public Result sendMessage(@RequestHeader("JK-Token") String jk_token, @RequestBody SendMessageDTO sendMessageDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("report_user")
    public Result reportUser(@RequestHeader("JK-Token") String jk_token, @RequestBody Map<String, String> map){
        Long targetId = Long.valueOf(map.get("targetId"));
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("like")
    public Result getLikeData(@RequestHeader(value = "JK-Token", required = false) String jk_token){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("notification")
    public Result getNotificationData(@RequestHeader(value = "JK-Token", required = false) String jk_token){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

}
