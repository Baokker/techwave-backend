package com.techwave.service;

import com.techwave.entity.SectionAndRequest;
import com.techwave.entity.dto.SolveDTO;
import com.techwave.entity.vo.SectionRequestsVO;
import com.techwave.entity.vo.SectionRequestsVO;
import com.techwave.utils.Result;
import java.util.List;

public interface AdminService {
    Result getReportList();
    Result getSectionRequest(int page,int perPage); // 获取版块申请列表
    List<SectionRequestsVO> getSectionRequestListWithPage(int page,int perPage); // 获取版块申请分页列表


    List<SectionRequestsVO> getSectionRequestsVOList(List<SectionAndRequest> sectionAndRequests);

    SectionRequestsVO sectionRequests2VO(SectionAndRequest sectionAndRequest);

    Result dealWithSectionRequests(SolveDTO solveDTO);

    Result approveSectionRequest(int sectionRequestId);

    Result rejectSectionRequest(int sectionRequestId);
}
