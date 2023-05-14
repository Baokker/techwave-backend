package com.techwave.controller;

import com.techwave.entity.Admin;
import com.techwave.entity.dto.SolveDTO;
import com.techwave.entity.dto.UnbanUserDTO;
import com.techwave.service.AdminService;
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
    @Autowired
    private AdminService adminService;

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
    public Result getSectionRequestData(@RequestHeader(value = "T-Token", required = false) String token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return adminService.getSectionRequest(page,perPage);
    }

    @GetMapping("report")
    public Result getReportData(@RequestHeader(value = "T-Token", required = false) String token, Integer page, Integer perPage){
        PageDTO pageDTO = new PageDTO(page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return adminService.getReportList(page,perPage);
    }

    @PostMapping ("section_request")
    public Result SectionRequest(@RequestHeader("T-Token") String token, @RequestBody SolveDTO solveDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return adminService.dealWithSectionRequests(solveDTO);
    }

    @PostMapping ("ban_user")
    public Result BanUser(@RequestHeader("T-Token") String token, @RequestBody BanUserDTO banUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        return adminService.banUser(banUserDTO);
    }

    @DeleteMapping("report/{reportId}")
    public Result deleteReportData(@RequestHeader(value = "T-Token", required = false) String token, @PathVariable Integer reportId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        System.out.println(reportId);
        return adminService.deleteReportData(reportId);
    }
    @GetMapping("ban_user")
    public Result getBanList(@RequestHeader(value = "T-Token",required = false) String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        return adminService.getBanList();
    }
    @PostMapping("unban_user")
    public Result unbanUser(@RequestHeader(value = "T-Token",required = false) String token ,@RequestBody UnbanUserDTO unbanUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Integer chatBanId=unbanUserDTO.getChatBanId();
        return adminService.unbanUser(chatBanId);
    }




}