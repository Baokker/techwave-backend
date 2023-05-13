package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminReportListVO {
    private Long reportId;
    private Long reportedUserId;
    private String reportType;
    private String reportReason;
    private String image;
    private String userName;
    private String avatar;
    private String summary;

    private Boolean isBanned;
}
