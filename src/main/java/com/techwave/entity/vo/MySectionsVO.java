package com.techwave.entity.vo;

import com.techwave.entity.SubSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/20 14:31
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySectionsVO {
    private Long sectionId;
    private String sectionName;
    private String sectionIntro;
    private String sectionAvatar;
    private Integer postCounts;
    private Integer userCounts;
    private List<SubSection> subSectionList;
}
