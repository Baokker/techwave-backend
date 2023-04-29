package com.techwave.service.impl;
/**
 * @author baokker
 * @date 2023/4/28
 */

import com.techwave.entity.ModeratorReport;
import com.techwave.entity.dto.ModeratorReportDTO;
import com.techwave.mapper.ModeratorReportMapper;
import com.techwave.service.ReportService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @descriptions: 举报
 * @author: baokker
 * @date: 2023/4/28 09:04
 * @version: 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ModeratorReportMapper moderatorReportMapper;

    @Override
    public Result createModeratorReport(ModeratorReportDTO moderatorReportDTO, Long userId) {
        ModeratorReport moderatorReport = new ModeratorReport();
        moderatorReport.setReportReason(moderatorReportDTO.getReportReason());
        moderatorReport.setReportSubtype(moderatorReportDTO.getReportSubtype());
        moderatorReport.setReportType(moderatorReportDTO.getReportType());
        moderatorReport.setSectionId(moderatorReportDTO.getSectionId());
        moderatorReport.setReportedId(moderatorReportDTO.getReportedId());
        moderatorReport.setUserId(userId);
        Integer result = moderatorReportMapper.insert(moderatorReport);
        if (result == 1) {
            return Result.success(TCode.SUCCESS.getCode(), "举报成功", null);
        }
        return Result.fail(TCode.FAIL.getCode(), "举报失败", null);
    }
}
