package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 回复的详细内容
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/5 0:02
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {
    private Long replyId;
    private LocalDateTime time;
    private String authorName;
    private Long authorId;
    private String toName;
    private Long toId;
    private String content;
    private Boolean ableToDelete;
}
