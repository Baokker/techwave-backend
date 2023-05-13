package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/12 22:14
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyListContentVO {
    private Long userId;
    private String avatar;
    private Integer count;
    private String recentChat;
}

