package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_section")
public class Section {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Long moderatorId;

    private String avatar;

    private Integer postCount;

    private Integer userCount;

}
