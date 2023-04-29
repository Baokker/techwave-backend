package com.techwave.entity.dto;
/**
 * @author baokker
 * @date 2023/4/28
 */

import com.techwave.entity.vo.ForumSectionVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @descriptions: 论坛首页的各个部分的DTO
 * @author: baokker
 * @date: 2023/4/28 08:21
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumSectionDTO {
    Integer total;
    List<ForumSectionVO> result;
}
