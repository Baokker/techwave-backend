package com.techwave.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_section_request")



public class SectionAndRequest {

    private Long id;

    private Long userId;

    private String name;

    private String avatar;

    private String description;

    private Boolean isApproved;

}
