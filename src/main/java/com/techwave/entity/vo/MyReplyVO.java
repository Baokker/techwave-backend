package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/19 14:19
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyReplyVO {
    private Long id;
    private String name;
    private String avatar;
    private String type;
    private String content;
    private LocalDateTime time;
}
