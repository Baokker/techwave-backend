package com.techwave.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class PageDTO {

    private Integer page;

    private Integer perPage;

}
