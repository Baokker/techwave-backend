package com.tjsse.jikespace.controller;

import com.tjsse.jikespace.entity.Admin;
import com.tjsse.jikespace.entity.dto.BanUserDTO;
import com.tjsse.jikespace.entity.dto.PageDTO;
import com.tjsse.jikespace.entity.dto.SectionDataDTO;
import com.tjsse.jikespace.entity.dto.SolveDTO;
import com.tjsse.jikespace.service.LoginService;
import com.tjsse.jikespace.utils.JKCode;
import com.tjsse.jikespace.utils.JwtUtil;
import com.tjsse.jikespace.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Result getSectionRequestData(@RequestHeader(value = "JK-Token", required = false) String jk_token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("report")
    public Result getReportData(@RequestHeader(value = "JK-Token", required = false) String jk_token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("section_request")
    public Result SectionRequest(@RequestHeader("JK-Token") String jk_token, @RequestBody SolveDTO solveDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("ban_user")
    public Result BanUser(@RequestHeader("JK-Token") String jk_token, @RequestBody BanUserDTO banUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return Result.fail(JKCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("report")
    public Result deleteReportData(@RequestHeader(value = "JK-Token", required = false) String jk_token, Integer reportId){
        String userIdStr = JwtUtil.getUserIdFromToken(jk_token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }




}