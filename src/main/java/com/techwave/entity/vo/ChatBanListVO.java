package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatBanListVO {
    private Long id;
    private Long userId;
    private String banUntil;
    private String userName;
}
