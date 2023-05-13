package com.techwave.controller;

import com.techwave.entity.dto.CollectPostDTO;
import com.techwave.entity.vo.MyReplyVO;
import com.techwave.entity.dto.SendMessageDTO;
import com.techwave.service.CommentService;
import com.techwave.service.MessageService;
import com.techwave.service.NotificationService;
import com.techwave.service.ReplyService;
import com.techwave.utils.TCode;
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
    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("reply")
    public Result getReply(@RequestHeader("T-Token") String token,Integer page,Integer perPage){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        List<MyReplyVO> myReplyVOList1 = replyService.findRepliesByUserId(userId);
        List<MyReplyVO> myReplyVOList2 = commentService.findCommentsByUserId(userId);
        myReplyVOList1.addAll(myReplyVOList2);

        myReplyVOList1.sort((t1,t2)->t2.getTime().compareTo(t1.getTime()));

        Set<MyReplyVO> collect = myReplyVOList1.stream()
                .skip((long) (page - 1) * perPage)
                .limit(perPage)
                .collect(Collectors.toSet());

        HashMap<String,Object> map = new HashMap<>();
        map.put("total",myReplyVOList1.size());
        map.put("myReply",collect);
        return Result.success(20000,"okk",map);
    }

    @GetMapping("count")
    //统计未读数
    public Result getCountData(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return notificationService.countNotRead(userId);
    }

    @GetMapping("list")
    public Result getListData(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return messageService.findListsById(userId);
    }

    @GetMapping("history")
    public Result getHistoryData(@RequestHeader("T-Token") String token, Integer targetId){
        //只有targetId就不封装了
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return messageService.findHistoriesById(userId, Long.valueOf(targetId));
    }

    @DeleteMapping("history")
    public Result deleteHistoryData(@RequestHeader(value = "T-Token", required = false) String token, Integer targetId){
        //只有targetId就不封装了
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("block")
    public Result blockUser(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map){
        Long targetId = Long.valueOf(map.get("targetId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("send_message")
    public Result sendMessage(@RequestHeader("T-Token") String token, @RequestBody SendMessageDTO sendMessageDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        return messageService.sendMessage(userId,sendMessageDTO);
    }

    @PostMapping("read")
    public Result readMessage(@RequestHeader("T-Token") String token,  @RequestBody Map<String, String> map) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        String type = map.get("type");
        return notificationService.readMessage(userId, type);
    }
    @PostMapping ("report_user")
    public Result reportUser(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map){
        Long targetId = Long.valueOf(map.get("targetId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("like")
    public Result getLikeData(@RequestHeader("T-Token") String token,Integer page,Integer perPage){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return notificationService.findNotificationsByTypeWithPage(userId,"like",page,perPage);
    }

    @GetMapping("notification")
    public Result getNotificationData(@RequestHeader("T-Token") String token,Integer page,Integer perPage){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return notificationService.findNotificationsByTypeWithPage(userId,"system",page,perPage);
    }

}
