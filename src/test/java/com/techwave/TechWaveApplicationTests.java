package com.techwave;

import com.techwave.entity.Admin;
import com.techwave.entity.User;
import com.techwave.mapper.AdminMapper;
import com.techwave.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
class TechWaveApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminMapper adminMapper;

    @Test
    void contextLoads() {
        List<User> list = userMapper.selectList(null);
        System.out.println(list);
    }

    @Test
    void registerAdmin() {
        Admin admin = Admin.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .build();
        int insert = adminMapper.insert(admin);
        System.out.println(insert == 1 ? "success" : "fail");
    }

}
