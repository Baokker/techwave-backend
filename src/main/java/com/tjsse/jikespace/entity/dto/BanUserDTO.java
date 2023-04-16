package com.tjsse.jikespace.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BanUserDTO {

    private Integer targetId;

    private String banUntil;

    private  String createdAt;

    private  Integer reportId;


}
