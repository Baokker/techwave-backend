package com.techwave.service;/**
 * @author baokker
 * @date 2023/4/28
 */

import com.techwave.entity.dto.ModeratorReportDTO;
import com.techwave.utils.Result;

/**
 * @descriptions: 举报
 * @author: baokker
 * @date: 2023/4/28 09:03
 * @version: 1.0
 */
public interface ReportService {
    Result createModeratorReport(ModeratorReportDTO moderatorReportDTO, Long userId);
}
