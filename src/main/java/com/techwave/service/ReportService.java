package com.techwave.service;/**
 * @author baokker
 * @date 2023/4/28
 */

import com.techwave.entity.dto.DeleteCommentDTO;
import com.techwave.entity.dto.ModeratorReportDTO;
import com.techwave.entity.dto.SectionDataDTO;
import com.techwave.entity.vo.PostReportDataVO;
import com.techwave.entity.vo.ReportDataVO;
import com.techwave.utils.Result;

import java.util.List;

/**
 * @descriptions: 举报
 * @author: baokker
 * @date: 2023/4/28 09:03
 * @version: 1.0
 */
public interface ReportService {
    Result createModeratorReport(ModeratorReportDTO moderatorReportDTO, Long userId);

    Result getCommentReportWithPage(SectionDataDTO sectionDataDTO);

    List<ReportDataVO> findReportBySectionIdWithPage(Long sectionId, Integer page, Integer perPage);

    List<PostReportDataVO> findPostReportBySectionIdWithPage(Long sectionId, Integer page, Integer perPage);

    Result getPostReportWithPage(SectionDataDTO sectionDataDTO);

    Result deleteReport(Long userId, Integer reportId);

    Result deletePost(Long userId, Integer targetId);

    Result deleteCommentOrReply(DeleteCommentDTO deleteCommentDTO);
}
