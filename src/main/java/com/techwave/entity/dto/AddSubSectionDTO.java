package com.techwave.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/20 15:00
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSubSectionDTO {
    private Long sectionId;
    private String[] subsections;
}
