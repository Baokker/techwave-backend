package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionReportVO {
    private Integer total;
    private List<ReportDataVO>  ReportDataVOList;
    private List<PostReportDataVO>  PostReportDataVOList;
}
