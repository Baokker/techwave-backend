package com.techwave.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SectionBanUserDTO {

    private Integer targetId;

    private Integer sectionId;

    private String banUntil;




}
