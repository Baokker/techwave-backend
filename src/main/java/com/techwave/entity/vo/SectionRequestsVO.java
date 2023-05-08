package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequestsVO {
    private Long id;

    private String userName;

    private String name;

    private String avatar;

    private String description;
}
