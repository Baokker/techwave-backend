package com.tjsse.jikespace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 回复
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/5 0:03
 * @since JDK18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_reply")
public class Reply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private Long commentId;
    private Long authorId;
    private Long toId;
    private String createdAt;
}
