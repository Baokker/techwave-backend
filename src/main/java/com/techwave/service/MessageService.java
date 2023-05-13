package com.techwave.service;

import com.techwave.entity.dto.PostDataDTO;
import com.techwave.entity.dto.SendMessageDTO;
import com.techwave.utils.Result;

import java.text.ParseException;

public interface MessageService {
    Result findListsById(Long userId);
    Result findHistoriesById(Long userId,Long targetId);
    Result sendMessage(Long userId, SendMessageDTO sendMessageDTO);
    Result createChat(Long userId, Long targetId);
}
