package com.techwave.service;

import com.techwave.utils.Result;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private LoginService loginService;
    @ParameterizedTest
    @CsvFileSource(resources = "/login_tests.csv", numLinesToSkip = 1)
    public void testCreateTokenByAccountOrEmail(String accountOrEmail, String password, String expectedCode, String expectedMsg) {
        Result result = loginService.createTokenByAccountOrEmail(accountOrEmail, password);
        if ("-1".equals(expectedCode)) {
            assertEquals(Integer.valueOf(-1), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        } else if ("20000".equals(expectedCode)) {
            assertEquals(Integer.valueOf(20000), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        }
    }
}