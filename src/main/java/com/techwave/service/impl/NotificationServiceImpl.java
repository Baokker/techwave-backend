package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.Notification;
import com.techwave.entity.User;
import com.techwave.entity.vo.MyLikeContentVO;
import com.techwave.entity.vo.MyLikeVO;
import com.techwave.entity.vo.MyNotificationContentVO;
import com.techwave.entity.vo.MyNotificationVO;
import com.techwave.mapper.NotificationMapper;
import com.techwave.mapper.UserMapper;
import com.techwave.service.NotificationService;
import com.techwave.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/5 9:10
 * @since JDK8
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result findNotificationsByTypeWithPage(Long userId, String type, Integer page, Integer perPage) {
        Page<Notification> notificationPage = new Page<>(page, perPage);
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getNotificationType, type);
        queryWrapper.eq(Notification::getUserId, userId);
        Page<Notification> notificationPage1 = notificationMapper.selectPage(notificationPage, queryWrapper);
        List<Notification> notificationList = notificationPage1.getRecords();
        //筛选未读数
//        LambdaQueryWrapper<Notification> queryWrapper1 = new LambdaQueryWrapper<>();
//        queryWrapper1.eq(Notification::getNotificationType, type);
//        queryWrapper1.eq(Notification::getUserId, userId);
//        queryWrapper1.eq(Notification::getIsRead, 0);
        MyNotificationVO myNotificationVO = new MyNotificationVO();
//        Integer count = Math.toIntExact(notificationMapper.selectCount(queryWrapper1));
        myNotificationVO.setTotal(notificationList.size());
        MyLikeVO myLikeVO = new MyLikeVO();
        myLikeVO.setTotal(notificationList.size());

        if(Objects.equals(type, "system")){
            myNotificationVO.setMyNotifications(copyToMyNotifications(notificationList));
            return Result.success(20000, "okk", myNotificationVO);
        } else if (Objects.equals(type, "like")) {
            myLikeVO.setMyLikes(copyToMyLikes(notificationList));
            return Result.success(20000, "okk", myLikeVO);
        }
        return Result.fail();
    }

    @Override
    public Result countNotRead(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getIsRead, false);
        queryWrapper.eq(Notification::getUserId, userId);
        return null;
    }

    private List<MyNotificationContentVO> copyToMyNotifications(List<Notification> notificationList) {
        List<MyNotificationContentVO> myNotificationContentVOS = new ArrayList<>();
        for (Notification notification :
                notificationList) {
            myNotificationContentVOS.add(copyToMyNotification(notification));
        }
        return myNotificationContentVOS;
    }

    private MyNotificationContentVO copyToMyNotification(Notification notification) {
        MyNotificationContentVO myNotificationContentVO = new MyNotificationContentVO();
        myNotificationContentVO.setNotificationId(notification.getId());
        myNotificationContentVO.setTitle(notification.getTitle());
        myNotificationContentVO.setCreateAt(notification.getCreatedAt());
        myNotificationContentVO.setContent(notification.getContent());
        return myNotificationContentVO;
    }

    private List<MyLikeContentVO> copyToMyLikes(List<Notification> notificationList) {
        List<MyLikeContentVO> myLikeContentVOS = new ArrayList<>();
        for (Notification notification :
                notificationList) {
            myLikeContentVOS.add(copyToMyLike(notification));
        }
        return myLikeContentVOS;
    }

    private MyLikeContentVO copyToMyLike(Notification notification) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, notification.getSenderId());
        MyLikeContentVO myLikeContentVO = new MyLikeContentVO();
        myLikeContentVO.setAvatar(userMapper.selectOne(queryWrapper).getAvatar());
        myLikeContentVO.setNotificationId(notification.getId());
        myLikeContentVO.setCreateAt(notification.getCreatedAt());
        myLikeContentVO.setContent(notification.getContent());
        myLikeContentVO.setLink(notification.getLink());
        return myLikeContentVO;
    }
}

