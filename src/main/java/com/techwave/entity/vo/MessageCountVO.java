package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/6 22:11
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageCountVO {
    private Long likeCount;

    private Long listCount;

    private Long notificationCount;

    private Long replyCount;

    private Long total;
}

