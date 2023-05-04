package com.techwave.service;

import com.techwave.entity.User;
import com.techwave.entity.dto.EditEmailDTO;
import com.techwave.entity.dto.PasswordDTO;
import com.techwave.entity.dto.UserInfoDTO;
import com.techwave.utils.Result;

/**
 * @program: JiKeSpace
 * @description:
 * @packagename: com.tjsse.jikespace.service
 * @author: peng peng
 * @date: 2022-12-02 15:22
 **/
public interface UserService {
    Result getUserInfo(String token);

    User findUserById(Long userId);

    Long findUserIdByCommentId(Long commentId);

    Result forgetPassword(String verifyCode, String email, String newPassword);

    Result sendEmailVerifyCode(String email);

    Result editEmail(Long userId, EditEmailDTO editEmailDTO);

    Result getUserInformation(Long userId);

    Result editUserInfo(Long userId, UserInfoDTO userInfoDTO);

    Result editPassword(Long userId, PasswordDTO passwordDTO);

    Result getUserCardInfo(Long userId);
}
