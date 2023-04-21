package com.techwave.entity.vo;

import com.techwave.entity.SubSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 版块数据及版块内的帖子数据
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 11:16
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDataVO {
    private String sectionName;

    private Integer postCounts;

    private Boolean isCollected;

    private String sectionSummary;

    private List<SubSection> subSectionList;

    private List<PostDataVO> postVOList;
}
