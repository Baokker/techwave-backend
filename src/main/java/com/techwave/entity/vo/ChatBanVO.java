package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatBanVO {
    Integer total;
    List<ChatBanListVO> chatBanListVO;
}
