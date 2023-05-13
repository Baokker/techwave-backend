package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.techwave.entity.*;
import com.techwave.entity.dto.SendMessageDTO;
import com.techwave.entity.vo.*;
import com.techwave.mapper.*;
import com.techwave.service.MessageService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/6 14:57
 * @since JDK8
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    ChatListMapper chatListMapper;
    @Autowired
    PrivateMessageMapper privateMessageMapper;
    @Autowired
    BlocklistMapper blocklistMapper;
    @Override
    public Result findListsById(Long userId) {
        LambdaQueryWrapper<ChatList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatList::getUser1Id, userId);
        queryWrapper.orderByDesc(ChatList::getRecentTime);
        List<ChatList> chatListList = chatListMapper.selectList(queryWrapper);
        MyListVO myListVO = new MyListVO();
        myListVO.setMyLists(copyToMyLists(chatListList));
        return Result.success(20000,"okk",myListVO);
    }

    @Override
    public Result findHistoriesById(Long userId,Long targetId) {
        LambdaQueryWrapper<PrivateMessage> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PrivateMessage::getRecipientId, userId);
        queryWrapper1.eq(PrivateMessage::getSenderId,targetId);
        List<PrivateMessage> messageList1 = privateMessageMapper.selectList(queryWrapper1);
        LambdaQueryWrapper<PrivateMessage> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(PrivateMessage::getSenderId, userId);
        queryWrapper2.eq(PrivateMessage::getRecipientId,targetId);
        List<PrivateMessage> messageList = privateMessageMapper.selectList(queryWrapper2);
        messageList.addAll(messageList1);
        messageList.sort(Comparator.comparing(PrivateMessage::getSendAt));
        HistoryVO historyVO = new HistoryVO();
        LambdaQueryWrapper<BlockList> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(BlockList::getBlockedUserId, userId);
        queryWrapper3.eq(BlockList::getUserId,targetId);
        historyVO.setIsBlocked(blocklistMapper.exists(queryWrapper3));
        historyVO.setMyHistories(copyToMyHistories(messageList,userId));
        return Result.success(20000,"okk",historyVO);
    }

    @Override
    public Result sendMessage(Long userId, SendMessageDTO sendMessageDTO) {
        //插入私信
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setMessageText(sendMessageDTO.getText());
        privateMessage.setSendAt(sendMessageDTO.getTime());
        privateMessage.setSenderId(userId);
        privateMessage.setRecipientId(Long.valueOf(sendMessageDTO.getTargetId()));
        int result1 = privateMessageMapper.insert(privateMessage);
        //插入消息通知
        Notification notification = new Notification();
        notification.setSenderId(userId);
        notification.setUserId(Long.valueOf(sendMessageDTO.getTargetId()));
        notification.setNotificationType("message");
        notification.setContent(sendMessageDTO.getText());
        notification.setIsRead(false);
        notification.setCreatedAt(sendMessageDTO.getTime());
        int result2 = notificationMapper.insert(notification);
        //修改chatList
        LambdaUpdateWrapper<ChatList> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(ChatList::getUser1Id,userId);
        updateWrapper1.eq(ChatList::getUser2Id,sendMessageDTO.getTargetId());
        updateWrapper1.set(ChatList::getRecentTime,sendMessageDTO.getTime());
        updateWrapper1.set(ChatList::getRecentChat,sendMessageDTO.getText());
        int result3 = chatListMapper.update(null,updateWrapper1);
        System.out.println("userId:"+ userId);
        System.out.println(sendMessageDTO);
        if(result3 != 1)
            return Result.fail(TCode.FAIL.getCode(), "给自己更新chatList失败", null);
        //查看是否已在对方关注列表
        LambdaUpdateWrapper<ChatList> updateWrapper2 = new LambdaUpdateWrapper<>();
        updateWrapper2.eq(ChatList::getUser1Id,sendMessageDTO.getTargetId());
        updateWrapper2.eq(ChatList::getUser2Id,userId);
        if(chatListMapper.exists(updateWrapper2))
        {
            updateWrapper2.set(ChatList::getRecentChat, sendMessageDTO.getText());
            updateWrapper2.set(ChatList::getRecentTime,sendMessageDTO.getTime());
            int result4 = chatListMapper.update(null,updateWrapper2);
            if(result4 != 1)
                return Result.fail(TCode.FAIL.getCode(), "给对方更新chatList失败", null);
        }
        else{
            //说明是第一次给对面发消息，给对方插入
            ChatList chatList1 = new ChatList();
            chatList1.setUser1Id(Long.valueOf(sendMessageDTO.getTargetId()));
            chatList1.setUser2Id(userId);
            chatList1.setRecentTime(sendMessageDTO.getTime());
            chatList1.setRecentChat(sendMessageDTO.getText());
            int result5 = chatListMapper.insert(chatList1);
            if(result5 != 1)
                return Result.fail(TCode.FAIL.getCode(), "给对方插入chatList失败", null);
        }
        if (result1 == 1 && result2 == 1) {
            return Result.success(TCode.SUCCESS.getCode(), "发送成功", null);
        }
        return Result.fail(TCode.FAIL.getCode(), "发送失败", null);
    }

    private List<MyListContentVO> copyToMyLists(List<ChatList> chatListList) {
        List<MyListContentVO> myListContentVOS = new ArrayList<>();
        for (ChatList chatList :
                chatListList) {
            myListContentVOS.add(copyToMyList(chatList));
        }
        return myListContentVOS;
    }
    private MyListContentVO copyToMyList(ChatList chatList) {
        MyListContentVO myListContentVO = new MyListContentVO();
        Long userId = chatList.getUser2Id();
        myListContentVO.setUserId(userId);
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getId, userId);
        myListContentVO.setAvatar(userMapper.selectOne(queryWrapper1).getAvatar());
        myListContentVO.setName(userMapper.selectOne(queryWrapper1).getUsername());
        myListContentVO.setRecentChat(chatList.getRecentChat());
        LambdaQueryWrapper<Notification> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Notification::getUserId,chatList.getUser1Id());
        queryWrapper2.eq(Notification::getSenderId,userId);
        queryWrapper2.eq(Notification::getIsRead,0);
        myListContentVO.setCount(Math.toIntExact(notificationMapper.selectCount(queryWrapper2)));
        return myListContentVO;
    }
    private List<HistoryContentVO> copyToMyHistories(List<PrivateMessage> messageList,Long userId) {
        List<HistoryContentVO> historyContentVOS = new ArrayList<>();
        for (PrivateMessage message :
                messageList) {
            historyContentVOS.add(copyToMyHistory(message,userId));
        }
        return historyContentVOS;
    }
    private HistoryContentVO copyToMyHistory(PrivateMessage message,Long userId) {
        HistoryContentVO historyContentVO = new HistoryContentVO();
        Long senderId = message.getSenderId();
        historyContentVO.setDate(message.getSendAt());
        historyContentVO.setMine(Objects.equals(senderId, userId));
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, senderId);
        User user = userMapper.selectOne(queryWrapper);
        historyContentVO.setImg(user.getAvatar());
        historyContentVO.setName(user.getUsername());
        Map<String,String> text = new HashMap<>();
        text.put("text",message.getMessageText());
        historyContentVO.setText(text);
        return historyContentVO;
    }
}

