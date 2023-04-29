package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/4/19 14:53
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_moderator_report")
public class ModeratorReport {
    @TableId(type = IdType.AUTO)
    private Long reportId;
    private Long userId;
    private Long sectionId;
    private Long reportedId;
    private String reportType;
    private String reportSubtype;
    private String reportReason;
    private String createdAt;
}

