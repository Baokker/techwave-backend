package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/13 0:01
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryContentVO {
    private String date;
    private Map<String,String> text;
    private Boolean mine;
    private String name;
    private String img;
}

