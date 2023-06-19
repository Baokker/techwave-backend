package com.techwave.service;

import com.techwave.entity.dto.PasswordDTO;
import com.techwave.entity.dto.UserInfoDTO;
import com.techwave.utils.Result;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @ParameterizedTest
    @CsvFileSource(resources = "/edit_info_tests.csv", numLinesToSkip = 1)
    public void testEditUserInfo(String phone, String username, String intro, String gender, String expectedCode, String expectedMsg) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(username);
        userInfoDTO.setPhone(phone);
        userInfoDTO.setIntro(intro);
        userInfoDTO.setGender(gender);

        Long userId = 6L;
        Result result = userService.editUserInfo(userId, userInfoDTO);

        if ("-1".equals(expectedCode)) {
            assertEquals(Integer.valueOf(-1), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        } else if ("20000".equals(expectedCode)) {
            assertEquals(Integer.valueOf(20000), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/edit_password_tests.csv", numLinesToSkip = 1)
    public void testEditPassword(String oldPassword, String newPassword, String expectedCode, String expectedMsg) {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword(oldPassword);
        passwordDTO.setNewPassword(newPassword);

        Long userId = 6L;
        Result result = userService.editPassword(userId, passwordDTO);

        if ("-1".equals(expectedCode)) {
            assertEquals(Integer.valueOf(-1), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        } else if ("20000".equals(expectedCode)) {
            assertEquals(Integer.valueOf(20000), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        }
    }
}