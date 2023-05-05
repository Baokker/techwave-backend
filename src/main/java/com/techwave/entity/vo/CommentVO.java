package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论详细内容
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/4 23:56
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Long commentId;
    private Long authorId;
    private String authorName;
    private String avatar;
    private String time;
    private String content;
    private Boolean ableToDelete;
    private List<ReplyVO> replyVOList;
}
