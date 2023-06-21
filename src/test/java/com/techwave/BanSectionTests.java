package com.techwave;
/**
 * @author baokker
 * @date 2023/4/29
 */

import com.techwave.service.BanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * @descriptions: 测试版块内封禁
 * @author: baokker
 * @date: 2023/4/29 14:23
 * @version: 1.0
 */
@SpringBootTest
public class BanSectionTests {
    @Autowired
    private BanService banService;

    @Test
    void banSectionUser() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
        System.out.println(banService.banSectionUser(1L, 1L, currentTimestamp));
    }

    @Test
    void unBanSectionABB() {
        System.out.println(banService.unBanSectionUser(1L, 1L));
    }

    @Test
    void getUserIs() {
        try {
            System.out.println(banService.getUserIsBannedInSection(1L, 1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
