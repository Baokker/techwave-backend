package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.techwave.entity.User;
import com.techwave.utils.TCode;
import com.techwave.utils.Result;
import com.techwave.mapper.UserMapper;
import com.techwave.service.FolderService;
import com.techwave.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    FolderService folderService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Result register(String username, String password, String email, String account) {
        List<User> userList;

        // username
        QueryWrapper<User> userNameWrapper = new QueryWrapper<>();
        userNameWrapper.eq("username", username);
        userList = userMapper.selectList(userNameWrapper);
        if (userList.size() >= 1) {
            return Result.fail(TCode.USERNAME_EXIST.getCode(), "用户名已存在", null);
        }

        // email
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", email);
        userList = userMapper.selectList(emailWrapper);
        if (userList.size() >= 1) {
            return Result.fail(TCode.EMAIL_EXIST.getCode(), "邮箱已存在", null);
        }

        // account
        QueryWrapper<User> accountWrapper = new QueryWrapper<>();
        accountWrapper.eq("account", username);
        userList = userMapper.selectList(accountWrapper);
        if (userList.size() >= 1) {
            return Result.fail(TCode.ACCOUNT_EXIST.getCode(), "账号已存在", null);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .avatar("https://moe.photo/images/2023/04/23/avatar.jpg")
                .account(account)
                .gender("未知")
                .phoneNumber("11111111111")
                .summary("这个人很懒，什么都没有留下~")
                .build();

        int insert = userMapper.insert(user);
        if (insert == 0 || insert < 0) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "用户注册失败");
        } else {
            folderService.createFolder(user.getId(), "默认收藏夹");
        }

        return Result.success(20000, "okk", null);
    }

    @Override
    public Boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

}
