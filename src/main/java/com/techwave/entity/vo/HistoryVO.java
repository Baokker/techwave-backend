package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaoming xxx@163.com
 * @version 2023/5/13 0:00
 * @since JDK8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryVO {
    private Boolean isBlocked;
    private List<HistoryContentVO> myHistories;
}

