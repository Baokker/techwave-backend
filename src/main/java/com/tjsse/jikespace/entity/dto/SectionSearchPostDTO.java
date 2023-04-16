package com.tjsse.jikespace.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionSearchPostDTO {
    private Long section_id;
    private Integer page;
    private Integer perPage;
    private String content;


}
