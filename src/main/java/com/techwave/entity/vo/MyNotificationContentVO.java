package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/5 9:27
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyNotificationContentVO {
    private Long notificationId;
    private String title;
    private String content;
    private String createAt;
}

