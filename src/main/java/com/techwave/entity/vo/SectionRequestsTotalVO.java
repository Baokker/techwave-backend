package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequestsTotalVO {
    private Integer total;
    private List<SectionRequestsVO> sectionRequestsVOList;
}
