package com.techwave.controller;

import com.techwave.entity.Admin;
import com.techwave.entity.dto.SolveDTO;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import com.techwave.entity.dto.BanUserDTO;
import com.techwave.entity.dto.PageDTO;
import com.techwave.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: JiKeSpace_backend
 * @description: controller for admin
 * @package_name: com.tjsse.jikespace.controller
 * @author: peng peng
 * @date: 2022/12/3
 **/

@RestController
@RequestMapping("admin/")
public class AdminController {
    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public Result login(@RequestBody Admin admin) {
        String password = admin.getPassword();
        String username = admin.getUsername();
        return loginService.createTokenByAdminName(username, password);
    }

    @GetMapping("info/")
    public String eq() {
        return "hello world";
    }

    @GetMapping("section_request")
    public Result getSectionRequestData(@RequestHeader(value = "JK-Token", required = false) String token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("report")
    public Result getReportData(@RequestHeader(value = "JK-Token", required = false) String token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("section_request")
    public Result SectionRequest(@RequestHeader("T-Token") String token, @RequestBody SolveDTO solveDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("ban_user")
    public Result BanUser(@RequestHeader("T-Token") String token, @RequestBody BanUserDTO banUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("report")
    public Result deleteReportData(@RequestHeader(value = "JK-Token", required = false) String token, Integer reportId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }




}