package com.techwave.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/4 11:04
 * @since JDK18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_subsection")
public class SubSection {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
}
