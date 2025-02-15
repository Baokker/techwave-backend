package com.techwave.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/6 15:14
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyOnPostDTO {
    private Long postId;
    private String content;
}
