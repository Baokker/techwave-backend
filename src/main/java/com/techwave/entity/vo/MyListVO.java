package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/12 22:39
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyListVO {
    private List<MyListContentVO> myLists;
}

