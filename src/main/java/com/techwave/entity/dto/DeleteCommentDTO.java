package com.techwave.entity.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentDTO {

    private Long userId;

    private Integer targetId;

    private String reportType;


}
