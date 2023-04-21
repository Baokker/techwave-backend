package com.techwave.entity.dto;
/**
 * @author baokker
 * @date 2023/4/21
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @descriptions: 登录表单dto
 * @author:
 * @date: 2023/4/21 13:59
 * @version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    String account;
    String password;
}
