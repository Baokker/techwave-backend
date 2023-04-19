package com.tjsse.jikespace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/4/19 14:49
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_moderator")
public class Moderator {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long sectionId;
    private String createdAt;
}

