package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "t_section_ban_user")
public class SectionBanUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sectionId;

    private String banUntil;

    private String createdAt;
}
