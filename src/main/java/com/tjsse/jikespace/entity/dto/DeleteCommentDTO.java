package com.tjsse.jikespace.entity.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentDTO {

    private Integer targetId;

    private String reportType;


}
