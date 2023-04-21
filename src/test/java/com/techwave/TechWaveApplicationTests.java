package com.techwave;

import com.techwave.entity.User;
import com.techwave.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TechWaveApplicationTests {

    @Autowired
    UserMapper userMapper;
    @Test
    void contextLoads() {
        List<User> list = userMapper.selectList(null);
        System.out.println(list);
    }

}
