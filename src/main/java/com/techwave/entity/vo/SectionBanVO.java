package com.techwave.entity.vo;

import com.techwave.entity.SubSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionBanVO {
    private Long id;

    private Long userId;

    private Long sectionId;

    private String banUntil;

    private String createdAt;

    private String userName;
}