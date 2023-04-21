package com.techwave.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.techwave.entity.dto.LoginDTO;
import com.techwave.entity.dto.UserDTO;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.RedisUtils;
import com.techwave.utils.Result;
import com.techwave.service.LoginService;
import com.techwave.service.RegisterService;
import com.techwave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @program: JiKeSpace
 * @description:
 * @packagename: com.tjsse.jikespace.controller.user
 * @author: peng peng
 * @date: 2022-12-02 10:53
 **/

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserService userInfoService;

    @PostMapping("/register")
    public Result registerUser(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String email = userDTO.getEmail();
        String account = userDTO.getAccount();

        return registerService.register(username, password, email, account);
    }

    @GetMapping("/send_email_code")
    public Result sendEmailCode(@RequestParam(value = "email") String email) {
        RedisUtils redisUtils = new RedisUtils(stringRedisTemplate);
        if (email == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "邮箱为空", null);
        }
        return userInfoService.sendEmailVerifyCode(email);
    }

    @PostMapping("/reset_password")
    public Result forgetPassword(@RequestBody JSONObject jsonObject) throws JSONException {
        String email = jsonObject.getString("email");
        String verifyCode = jsonObject.getString("verifyCode");
        String newPassword = jsonObject.getString("newPassword");

        if (email == null || verifyCode == null || newPassword == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        }
        return userInfoService.forgetPassword(verifyCode, email, newPassword);
    }

    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("T-Token") String token) {
        return userInfoService.getUserInfo(token);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String accountOrEmail = loginDTO.getAccount();
        String password = loginDTO.getPassword();
        return loginService.createTokenByAccountOrEmail(accountOrEmail, password);
    }

    @PostMapping("/logout")
    public Result logout(@RequestHeader("T-Token") String token) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Integer userId = Integer.parseInt(userIdStr);
        return loginService.logout(userId);
    }
}
