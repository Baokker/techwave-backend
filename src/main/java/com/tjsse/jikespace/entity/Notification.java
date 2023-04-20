package com.tjsse.jikespace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "t_notification")
public class Notification {
    private Long id;

    private Long userId;

    private Long sendId;

    private String notificationType;

    private String content;

    private String link;

    private Boolean isRead;

    private String createdAt;


}
