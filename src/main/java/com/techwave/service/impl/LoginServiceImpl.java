package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.techwave.entity.Admin;
import com.techwave.entity.User;
import com.techwave.utils.JKCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import com.techwave.mapper.AdminMapper;
import com.techwave.mapper.UserMapper;
import com.techwave.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: JiKeSpace
 * @description:
 * @packagename: com.tjsse.jikespace.service.impl
 * @author: peng peng
 * @date: 2022-12-02 10:50
 **/

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    // 删除此代码后无法运行，虽然提示没有用到，但是由于自动注入，配置类会用到，不可删@！！！！
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Result createTokenByAdminName(String adminName, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin_name", adminName);
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin == null) {
            return Result.fail(JKCode.ACCOUNT_NOT_EXIST.getCode(), JKCode.ACCOUNT_EXIST.getMsg(), null);
        }

        String jwt = JwtUtil.createJWT(admin.getId().toString(), "admin");

        if(jwt == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "token 生成失败", null);
        }
        return Result.success(JKCode.SUCCESS.getCode(), JKCode.SUCCESS.getMsg(), jwt);
    }

    @Override
    public Result createTokenByEmail(String email, String password) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return Result.fail(JKCode.ACCOUNT_NOT_EXIST.getCode(), "用户不存在", null);
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            return Result.fail(JKCode.PWD_ERROR.getCode(), JKCode.PWD_ERROR.getMsg(), null);
        }

        // 修改用户登录信息
        LocalDateTime lastLoginTime = LocalDateTime.now();

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", user.getId())
                .set("last_login_time", lastLoginTime)
                .set("status", JKCode.LOG_IN.getCode());
        userMapper.update(null, userUpdateWrapper);

        String jwt = JwtUtil.createJWT(user.getId().toString(), "user");

        if(jwt == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "token 生成失败", null);
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);

        return Result.success(JKCode.SUCCESS.getCode(), JKCode.SUCCESS.getMsg(), map);
    }

    @Override
    public Result logout(Integer userId) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", userId)
                .ne("status", JKCode.LOG_OUT.getCode())
                .set("status", JKCode.LOG_OUT.getCode());
        int res = userMapper.update(null, userUpdateWrapper);
        if (res == 0) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "用户未找到或该用户已经登出", null);
        } else if (res > 1) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "登出多个用户", null);
        } else {
            return Result.success(JKCode.SUCCESS.getCode(), JKCode.SUCCESS.getMsg(), null);
        }
    }
}
