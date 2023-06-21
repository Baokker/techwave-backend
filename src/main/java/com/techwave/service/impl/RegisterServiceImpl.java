package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.techwave.entity.User;
import com.techwave.utils.TCode;
import com.techwave.utils.Result;
import com.techwave.mapper.UserMapper;
import com.techwave.service.FolderService;
import com.techwave.service.RegisterService;
import org.apache.ibatis.annotations.AutomapConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
        String validRegex = "[^a-zA-Z0-9]";
        if (username == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "user name is empty", null);
        }
        if (password == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "password is empty", null);
        }
        if (email == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "email is empty", null);
        }
        if (account == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "account is empty", null);
        }

        if(!this.isEmail(email)){
            return Result.fail(-1, "email is invalid",null);
        }

        // username
        if (username.length() > 10) {
            return Result.fail(-1, "user name is too long", null);
        }
        QueryWrapper<User> userNameWrapper = new QueryWrapper<>();
        userNameWrapper.eq("account", account);
        userList = userMapper.selectList(userNameWrapper);
        if (userList.size() >= 1) {
            return Result.fail(-1, "account already exists", null);
        }

        // email
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", email);
        userList = userMapper.selectList(emailWrapper);
        if (userList.size() >= 1) {
            return Result.fail(-1, "email already exists", null);
        }

        if (password.length() < 8) {
            return Result.fail(-1, "password is too short", null);
        }
        if (password.length() > 20) {
            return Result.fail(-1, "password is too long", null);
        }

        if(password == null || password.equals("") || username==null || username.equals("") || account == null|| account.equals("")) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        }

        // account
        QueryWrapper<User> accountWrapper = new QueryWrapper<>();
        accountWrapper.eq("account", username);
        userList = userMapper.selectList(accountWrapper);
        if (userList.size() >= 1) {
            return Result.fail(-1, "account already exists", null);
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
            return Result.fail(TCode.OTHER_ERROR.getCode(), "Register Fail");
        } else {
            folderService.createFolder(user.getId(), "默认收藏夹");
        }

        return Result.success(20000, "Success", null);
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
