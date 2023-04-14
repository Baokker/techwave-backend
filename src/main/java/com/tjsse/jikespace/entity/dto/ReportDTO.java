package com.tjsse.jikespace.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/4/14 11:03
 * @since JDK8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String reportReason;
    private String reportSubtype;
    private String reportType;
}

