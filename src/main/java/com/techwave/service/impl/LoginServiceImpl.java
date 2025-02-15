package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.techwave.entity.Admin;
import com.techwave.entity.User;
import com.techwave.utils.TCode;
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

/**
 * @program: TechWave
 * @description:
 * @packagename: com.techwave.service.impl
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
        queryWrapper.eq("username", adminName);
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin == null) {
            return Result.fail(TCode.ACCOUNT_NOT_EXIST.getCode(), TCode.ACCOUNT_EXIST.getMsg(), null);
        }

        String jwt = JwtUtil.createJWT(admin.getId().toString());

        if (jwt == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "token 生成失败", null);
        }
        return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), jwt);
    }

    @Override
    public Result createTokenByAccountOrEmail(String accountOrEmail, String password) {
        QueryWrapper<User> queryEmailWrapper = new QueryWrapper<>();
        queryEmailWrapper.eq("email", accountOrEmail);
        User user = userMapper.selectOne(queryEmailWrapper);

        if (user == null) {
            QueryWrapper<User> queryAccountWrapper = new QueryWrapper<>();
            queryAccountWrapper.eq("account", accountOrEmail);
            user = userMapper.selectOne(queryAccountWrapper);
            if (user == null) {
                return Result.fail(TCode.ACCOUNT_NOT_EXIST.getCode(), "User does not exist", null);
            }
        }

        if (password == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "password is empty", null);
        }
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            return Result.fail(TCode.PWD_ERROR.getCode(), TCode.PWD_ERROR.getMsg(), null);
        }

        // 修改用户登录信息
        LocalDateTime lastLoginTime = LocalDateTime.now();

        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", user.getId())
                .set("last_login_time", lastLoginTime)
                .set("status", TCode.LOG_IN.getCode());

        userMapper.update(null, userUpdateWrapper);

        String jwt = JwtUtil.createJWT(user.getId().toString());

        if (jwt == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "token create failed", null);
        }

        return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), jwt);
    }

    @Override
    public Result logout(Integer userId) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", userId)
                .ne("status", TCode.LOG_OUT.getCode())
                .set("status", TCode.LOG_OUT.getCode());
        userMapper.update(null, userUpdateWrapper);
        //if (res == 0) {
        //    return Result.fail(TCode.OTHER_ERROR.getCode(), "用户未找到或该用户已经登出", null);
        //} else if (res > 1) {
        //    return Result.fail(TCode.OTHER_ERROR.getCode(), "登出多个用户", null);
        //} else {
            return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), null);
        //}
    }
}
