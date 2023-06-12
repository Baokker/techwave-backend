package com.techwave.service.impl;

import com.techwave.entity.dto.UserInfoDTO;
import com.techwave.utils.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }
    @ParameterizedTest
    @CsvFileSource(resources = "/1.csv", numLinesToSkip = 1)
    void testEditUserInfo(String phone, String username, String intro, String gender) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(username);
        userInfoDTO.setPhone(phone);
        userInfoDTO.setIntro(intro);
        userInfoDTO.setGender(gender);

        Result result = userService.editUserInfo(1L, userInfoDTO);
        assertEquals(20000, result.getCode());
        assertNull(result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void editPassword() {
    }
}