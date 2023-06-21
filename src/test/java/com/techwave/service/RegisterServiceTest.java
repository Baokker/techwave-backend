package com.techwave.service;

import com.techwave.utils.Result;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RegisterServiceTest {
    @Autowired
    private RegisterService registerService;
    @ParameterizedTest
    @CsvFileSource(resources = "/register_tests.csv", numLinesToSkip = 1)
    void TestRegister(String username, String password, String email, String account, String expectedCode, String expectedMsg) {
        Result result = registerService.register(username, password, email, account);

        if ("-1".equals(expectedCode)) {
            assertEquals(Integer.valueOf(-1), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        } else if ("20000".equals(expectedCode)) {
            assertEquals(Integer.valueOf(20000), result.getCode());
            assertEquals(expectedMsg, result.getMsg());
        }
    }
}