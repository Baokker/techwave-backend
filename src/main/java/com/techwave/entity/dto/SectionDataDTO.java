package com.techwave.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取版块信息请求参数
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 0:30
 * @since JDK18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDataDTO {
    private Long sectionId;

    private Integer page;

    private Integer perPage;
}
