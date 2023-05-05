package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/5 9:32
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyLikeContentVO {
    private Long notificationId;
    private String avatar;
    private String link;
    private String content;
    private String createAt;
}

