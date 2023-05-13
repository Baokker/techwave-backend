package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/12 20:39
 * @since JDK8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_chat_list")
public class ChatList {
    @TableId(type = IdType.AUTO)
    private Long id;
    //自己
    private Long user1Id;
    //好友
    private Long user2Id;
    private String recentChat;
    private String recentTime;
}

