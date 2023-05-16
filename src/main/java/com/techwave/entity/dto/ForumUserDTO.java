package com.techwave.entity.dto;


import com.techwave.entity.vo.ForumSectionVO;
import com.techwave.entity.vo.ForumUserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumUserDTO {

    Integer total;
    List<ForumUserVO> result;


}
