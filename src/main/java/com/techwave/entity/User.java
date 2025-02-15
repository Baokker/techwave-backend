package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private Boolean isModerator;

    private String summary;

    private String avatar;

    private LocalDateTime createTime;

    private Boolean isDeleted;

    private String email;

    private LocalDateTime lastLoginTime;

    private String phoneNumber;

    private String account;

    private String password;

    private String gender;

    private String status;

    private Integer followCount;

    private Integer fanCount;
}
