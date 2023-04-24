package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.techwave.entity.Admin;
import com.techwave.entity.Comment;
import com.techwave.entity.Section;
import com.techwave.entity.User;
import com.techwave.entity.dto.EditEmailDTO;
import com.techwave.entity.dto.PasswordDTO;
import com.techwave.entity.dto.UserInfoDTO;
import com.techwave.entity.vo.UserDataVO;
import com.techwave.entity.vo.UserVO;
import com.techwave.mapper.AdminMapper;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import com.techwave.mapper.CommentMapper;
import com.techwave.mapper.SectionMapper;
import com.techwave.mapper.UserMapper;
import com.techwave.service.EmailService;
import com.techwave.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: JiKeSpace
 * @description:
 * @packagename: com.tjsse.jikespace.service.impl
 * @author: peng peng
 * @date: 2022-12-02 15:24
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Result getUserInfo(String token) {
        String userIdFromToken = JwtUtil.getUserIdFromToken(token);

        assert userIdFromToken != null;

        Integer userId = Integer.parseInt(userIdFromToken);

        List<Map<String, Object>> roles = new ArrayList<>();

        // check if is admin
        Admin admin = adminMapper.selectById(userId);
        if (admin != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "admin");
            map.put("sectionId", null);
            roles.add(map);

            UserVO userVO = new UserVO();
            userVO.setUsername(admin.getUsername());
            userVO.setRoles(roles);
            return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), userVO);
        }

        UserVO userVO = new UserVO();
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "用户不存在", null);
        }
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());


        // check if is moderator
        if (user.getIsModerator()) {
            LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Section::getModeratorId, userIdFromToken);
            List<Section> sections = sectionMapper.selectList(queryWrapper);
            for (Section section : sections) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("name", "moderator");
                //roles.add(map1);
                //Map<String, Object> map2 = new HashMap<>();
                map1.put("sectionId", section.getId());
                roles.add(map1);
            }
        }

        // if not admin or moderator
        if (roles.size() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "user");
            map.put("sectionId", null);
            roles.add(map);
        }

        userVO.setRoles(roles);
        return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), userVO);
    }

    @Override
    public User findUserById(Long userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Long findUserIdByCommentId(Long commentId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getId, commentId);
        queryWrapper.last("limit 1");
        Comment comment = commentMapper.selectOne(queryWrapper);
        return comment.getAuthorId();
    }

    @Override
    public Result forgetPassword(String verifyCode, String email, String newPassword) {
        // 检测验证码与邮箱是否正确
        boolean checkResult = emailService.checkVerifyCode(email, verifyCode);
        if (!checkResult) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "验证码错误", null);
        }
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("email", email);
        userUpdateWrapper.set("password", passwordEncoder.encode(newPassword));
        userMapper.update(null, userUpdateWrapper);
        return Result.success(TCode.SUCCESS.getCode(), TCode.SUCCESS.getMsg(), null);
    }

    @Override
    public Result sendEmailVerifyCode(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email)
                .eq("is_deleted", false);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return Result.fail(TCode.ACCOUNT_NOT_EXIST.getCode(), "该邮箱未使用，请先注册", null);
        }
        try {
            emailService.sendEmailVerifyCode(email);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(TCode.OTHER_ERROR.getCode(), "发送邮件失败", null);
        }
        return Result.success(TCode.SUCCESS.getCode(), "发送验证码成功", null);
    }

    @Override
    public Result editEmail(Long userId, EditEmailDTO editEmailDTO) {
        String email = editEmailDTO.getEmail();
        String password = editEmailDTO.getPassword();
        if (email == null || password == null) {
            return Result.fail(-1, "参数有误", null);
        }

        User user = this.findUserById(userId);
        String password1 = user.getPassword();
        boolean matches = passwordEncoder.matches(password, password1);
        if (matches) {
            if (Objects.equals(user.getEmail(), email)) {
                Map<String, Boolean> map = new HashMap<>();
                map.put("result", false);
                return Result.fail(-1, "新邮箱与原邮箱重复", map);
            }
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, email);
            queryWrapper.eq(User::getIsDeleted, false);
            List<User> users = userMapper.selectList(queryWrapper);
            if (users.size() != 0) {
                Map<String, Boolean> map = new HashMap<>();
                map.put("result", false);
                return Result.fail(-1, "该邮箱已与其他账号绑定", map);
            }
            user.setEmail(email);
            userMapper.updateById(user);
            Map<String, Boolean> map = new HashMap<>();
            map.put("result", true);
            return Result.success(20000, "okk", map);
        } else {
            Map<String, Boolean> map = new HashMap<>();
            map.put("result", false);
            return Result.success(20000, "密码错误", map);
        }
    }

    @Override
    public Result getUserInformation(Long userId) {
        User user = this.findUserById(userId);

        UserDataVO userVO = new UserDataVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(20000, "okk", userVO);
    }

    @Override
    public Result editUserInfo(Long userId, UserInfoDTO userInfoDTO) {
        String username = userInfoDTO.getUsername();
        String phone = userInfoDTO.getPhone();
        String intro = userInfoDTO.getIntro();
        String gender = userInfoDTO.getGender();
        if (username == null || phone == null || intro == null || gender == null) {
            return Result.fail(-1, "参数有误", null);
        }

        User user = this.findUserById(userId);

        user.setUsername(username);
        user.setPhoneNumber(phone);
        user.setGender(gender);
        user.setSummary(intro);

        userMapper.updateById(user);

        return Result.success(20000, "okk", null);
    }

    @Override
    public Result editPassword(Long userId, PasswordDTO passwordDTO) {
        String newPassword = passwordDTO.getNewPassword();
        String oldPassword = passwordDTO.getOldPassword();
        if (newPassword == null || oldPassword == null) {
            return Result.fail(-1, "参数错误", null);
        }
        User user = this.findUserById(userId);
        String password = user.getPassword();
        boolean matches = passwordEncoder.matches(oldPassword, password);
        if (matches) {
            if (oldPassword == newPassword) {
                Map<String, Boolean> map = new HashMap<>();
                map.put("result", false);
                return Result.fail(-1, "新密码与旧密码重复", map);
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userMapper.updateById(user);
            Map<String, Boolean> map = new HashMap<>();
            map.put("result", true);
            return Result.success(20000, "okk", map);
        } else {
            Map<String, Boolean> map = new HashMap<>();
            map.put("result", false);
            return Result.fail(-1, "密码错误或新密码与旧密码重复", map);
        }
    }

}
