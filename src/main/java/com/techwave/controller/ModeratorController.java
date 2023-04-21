package com.techwave.controller;


import com.techwave.entity.Section;
import com.techwave.entity.dto.*;
import com.techwave.utils.JwtUtil;
import com.techwave.service.SectionService;
import com.techwave.utils.TCode;
import com.techwave.utils.OssService;
import com.techwave.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("moderator/")
public class ModeratorController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private OssService ossService;

    @PostMapping("add_subsection")
    public Result addSubSection(@RequestHeader("T-Token") String token, @RequestBody AddSubSectionDTO addSubSectionDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        Long sectionId = addSubSectionDTO.getSectionId();
        Section sectionById = sectionService.findSectionById(sectionId);
        if(Objects.equals(sectionById.getModeratorId(), userId)){
            return sectionService.addSubSection(addSubSectionDTO);
        }
        return Result.fail(-1,"该用户没有此权限",null);
    }

    @DeleteMapping("delete_subsection")
    public Result deleteSubSection(@RequestHeader("T-Token") String token,@RequestBody Map<String,Integer> map){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        Integer subsectionId = map.get("subsectionId");
        return sectionService.deleteSubSection(userId,subsectionId);
    }


    @PostMapping("rename_subsection")
    public Result renameSubSection(@RequestHeader("T-Token") String token,@RequestBody RenameSubSectionDTO renameSubSectionDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        renameSubSectionDTO.setUserId(userId);
        return sectionService.renameSubSection(renameSubSectionDTO);
    }

    @PostMapping("edit_avatar")
    public Result changeSectionAvatar(@RequestHeader("T-Token") String token,@RequestParam("sectionId") Long sectionId
            ,@RequestParam("avatar") MultipartFile image){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        String avatar = ossService.uploadFile(image);
        return sectionService.changeSectionAvatar(userId,sectionId,avatar);
    }

    @PostMapping("edit_description")
    public Result changeSectionIntro(@RequestHeader(value = "JK-Token") String token,@RequestBody ChangeIntroDTO changeIntroDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return sectionService.changeSectionIntro(userId,changeIntroDTO);
    }

    @GetMapping("subsection")
    public Result getSubsection(@RequestHeader("T-Token") String token,Integer sectionId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping("edit_section_name")
    public Result editSectionName(@RequestHeader(value = "JK-Token") String token,@RequestBody SectionNameDTO sectionNameDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("section/{{sectionId}}")
    public Result getSectionData(@RequestHeader("T-Token") String token,@PathVariable Integer sectionId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("search")
    public Result getUserBySearch(@RequestHeader(value = "JK-Token") String token,Integer page,Integer perPage,String content){

        SearchUserDTO searchUserDTO = new SearchUserDTO(content,page,perPage);

        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping("transfer_section")
    public Result TransferSection(@RequestHeader(value = "JK-Token") String token,@RequestBody TransferSectionDTO transferSectionDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("post_report")
    public Result getPostReport(@RequestHeader(value = "JK-Token") String token,Integer page,Integer perPage,Integer sectionId){

        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)sectionId,page,perPage);

        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("comment_report")
    public Result getCommentReport(@RequestHeader(value = "JK-Token") String token,Integer page,Integer perPage,Integer sectionId){

        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)sectionId,page,perPage);

        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("post_report")
    public Result deletePostReportData(@RequestHeader(value = "JK-Token", required = false) String token, Integer reportId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("post")
    public Result deletePostData(@RequestHeader(value = "JK-Token", required = false) String token, Integer targetId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("comment")
    public Result deleteReplyData(@RequestHeader(value = "JK-Token", required = false) String token, @RequestBody DeleteCommentDTO deleteCommentDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("comment_report")
    public Result deleteCommentReportData(@RequestHeader(value = "JK-Token", required = false) String token, Integer reportId){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("{{section_id}}/pinned_post")
    public Result getPinnedPosts(@RequestHeader("T-Token") String token,@PathVariable Integer section_id,Integer page,Integer perPage){
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)section_id,page,perPage);
        return null;
    }

    @GetMapping("{{section_id}}/highlighted_post")
    public Result getHighlightedPosts(@PathVariable Integer section_id,Integer page,Integer perPage){
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)section_id,page,perPage);
        return null;
    }

    @PostMapping("pin_post")
    public Result PinPost(@RequestHeader(value = "JK-Token") String token,@RequestBody Map<String, Integer> map){
        Integer postId = Integer.valueOf(map.get("postId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping("highlight_post")
    public Result HighlightPost(@RequestHeader(value = "JK-Token") String token,@RequestBody Map<String, Integer> map){
        Integer postId = Integer.valueOf(map.get("postId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @GetMapping("banned_user")
    public Result getAllBannedUser(@RequestHeader(value = "JK-Token") String token,Integer section_id){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @PostMapping ("ban_user")
    public Result BanUser(@RequestHeader("T-Token") String token, @RequestBody SectionBanUserDTO sectionBanUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

    @DeleteMapping("ban_user")
    public Result deleteBanUserData(@RequestHeader(value = "JK-Token", required = false) String token, @RequestBody DeleteBanUserDTO DeleteBanUserDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return null;
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }

}
