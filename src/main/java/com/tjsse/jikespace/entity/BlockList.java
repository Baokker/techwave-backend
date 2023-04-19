package com.tjsse.jikespace.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author xiaoming xxx@163.com
 * @version 2023/4/19 14:30
 * @since JDK8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blocklist")
public class BlockList {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private Long blockedUserId;
}

