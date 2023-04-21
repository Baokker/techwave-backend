package com.techwave.controller;

import com.techwave.entity.User;
import com.techwave.entity.dto.*;
import com.techwave.service.*;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.OssService;
import com.techwave.utils.Result;
import com.techwave.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


/**
 * @program: JiKeSpace
 * @description: 获取用户基本信息
 * @packagename: com.tjsse.jikespace.controller.user
 * @author: peng peng
 * @date: 2022-12-02 15:21
 **/
@RestController
@RequestMapping("/account/")
public class AccountController {
    @Autowired
    private UserService userInfoService;
    @Autowired
    private SectionService sectionService;

    @Autowired
    private PostService postService;
    @Autowired
    private OssService ossService;

    @Autowired
    EmailService emailService;
    @Autowired
    private  FolderService folderService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("edit_email")
    public Result editEmail(@RequestHeader("T-Token") String token, @RequestBody EditEmailDTO editEmailDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return userInfoService.editEmail(userId,editEmailDTO);
    }

    @GetMapping("info")
    public Result getUserInformation(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return userInfoService.getUserInformation(userId);
    }
    @GetMapping("{{target_id}}/card")
    public Result getUserCard(@RequestHeader("T-Token") String token, @PathVariable String target_id){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }
    @PostMapping("change_avatar")
    public Result changeAvatar(@RequestHeader("T-Token") String token,@RequestParam("Avatar") MultipartFile avatar){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);

        String s = ossService.uploadFile(avatar);
        User user = userInfoService.findUserById(userId);
        user.setAvatar(s);
        threadService.updateUserByAvatar(userMapper,user);
        Map<String,String> map = new HashMap<>();
        map.put("result",s);

        return Result.success(20000,"okk",map);
    }

    @PostMapping("edit_info")
    public Result editUserInfo(@RequestHeader("T-Token") String token, @RequestBody UserInfoDTO userInfoDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return userInfoService.editUserInfo(userId,userInfoDTO);
    }

    @PostMapping("edit_password")
    public Result editPassword(@RequestHeader("T-Token") String token, @RequestBody PasswordDTO passwordDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return userInfoService.editPassword(userId,passwordDTO);
    }

    @PostMapping("create_folder")
    public Result createFolder(@RequestHeader("T-Token") String token, @RequestBody Map<String,String> map){
        String folderName = map.get("folderName");
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return folderService.createFolder(userId,folderName);
    }

    @PostMapping("rename_folder")
    public Result renameFolder(@RequestHeader("T-Token") String token, @RequestBody RenameFolderDTO renameFolderDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        return folderService.renameFolder(renameFolderDTO);
    }

    @GetMapping("folder")
    public Result getFolders(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return folderService.getFolders(userId);
    }

    @GetMapping(value = "collect")
    public Result getCollectInfo(@RequestHeader("T-Token") String token,Long folderId
            ,Integer curPage, Integer limit){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        FolderPostDTO folderPostDTO = new FolderPostDTO(folderId,curPage,limit);
        return folderService.getCollectInfo(userId,folderPostDTO);
    }

    @DeleteMapping("folder")
    public Result deleteFolder(@RequestHeader("T-Token") String token,@RequestBody Map<String,String> map){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        Long folderId = Long.valueOf(map.get("folderId"));
        return folderService.deleteFolder(userId,folderId);
    }

    @GetMapping("post")
    public Result getMyPost(@RequestHeader("T-Token") String token,String type,Integer curPage,Integer limit){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return  postService.findPostsByUserIdWithPage(userId,type,curPage,limit);
    }

    @DeleteMapping("post")
    public Result deleteMyPost(@RequestHeader("T-Token") String token,@RequestBody Map<String,Long> map){
        Long postId = map.get("postId");
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return postService.deleteMyPost(postId,userId);
    }

    @GetMapping("section")
    public Result getUserSections(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return sectionService.getUserSections(userId);
    }

    @PostMapping("create_section")
    public Result createSection(@RequestHeader("T-Token") String token,@RequestParam("sectionName") String sectionName
            ,@RequestParam("sectionAvatar") MultipartFile image,@RequestParam("sectionIntro") String sectionIntro
    ,@RequestParam("subsections") String[] subsection){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        String s = ossService.uploadFile(image);
        return sectionService.createSection(userId,sectionName,s,sectionIntro,subsection);
    }

}

