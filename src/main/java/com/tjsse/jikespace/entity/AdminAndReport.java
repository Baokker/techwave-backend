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
 * @version 2023/4/19 14:23
 * @since JDK8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_admin_report")
public class AdminAndReport {
    @TableId(type = IdType.AUTO)
    private Long reportId;
    private Long userId;
    private Long reportedUserId;
    private String reportType;
    private String reportReason;
    private String createAt;
    private String image;
}

