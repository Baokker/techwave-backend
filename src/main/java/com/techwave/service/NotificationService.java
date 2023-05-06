package com.techwave.service;

import com.techwave.utils.Result;

public interface NotificationService {
    Result findNotificationsByTypeWithPage(Long userId, String type, Integer page, Integer perPage);
    Result countNotRead(Long userId);
}
