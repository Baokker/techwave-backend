package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDataVO {
    private Long id;

    private Long userId;

    private String reportType;

    private Long reportedId;

    private String reportSubtype;

    private String reportReason;

    private LocalDateTime createdAt;

    private String commentContent;

}
