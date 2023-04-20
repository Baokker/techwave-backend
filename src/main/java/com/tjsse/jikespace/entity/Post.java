package com.tjsse.jikespace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 帖子数据
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 13:36
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "jk_post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authorId;

    private String title;

    private String content;

    private String CreatedAt;

    private Boolean isPinned;

    private Boolean isHighlighted;

    private Long sectionId;

    private Long subsectionId;

    private Integer commentCounts;

    private Boolean isDeleted;

    private Integer viewCounts;

    private Long bodyId;

    private LocalDateTime updateTime;


}
