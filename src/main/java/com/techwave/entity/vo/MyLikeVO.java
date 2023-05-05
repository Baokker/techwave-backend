package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/5 9:32
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyLikeVO {
    private Integer total;
    private List<MyLikeContentVO> myLikes;
}

