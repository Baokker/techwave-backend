package com.tjsse.jikespace.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author xiaoming xxx@163.com
 * @version 2023/4/19 14:46
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_follow_section")
public class FollowAndSection {
    @TableId(type = IdType.AUTO)
    private Long sectionId;
    private Long followerId;
}

