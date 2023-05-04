package com.techwave.entity.vo;
/**
 * @author baokker
 * @date 2023/5/4
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @descriptions:
 * @author: baokker
 * @date: 2023/5/4 13:19
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCardVO {
    Long id;
    String username;
    String account;
    Integer followCount;
    Integer fanCount;
    String summary;
}
