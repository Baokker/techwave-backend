package com.techwave.controller;

import com.techwave.entity.dto.PostPublishDTO;
import com.techwave.entity.dto.PostsWithTagDTO;
import com.techwave.entity.dto.SectionDataDTO;
import com.techwave.entity.dto.SectionSearchPostDTO;
import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import com.techwave.service.CollectService;
import com.techwave.service.PostService;
import com.techwave.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

/**
 * 版块的控制类
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 11:40
 * @since JDK18
 */
@RestController
@RequestMapping("section/")
public class SectionController {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CollectService collectService;
    @Autowired
    private PostService postService;

    @GetMapping("{sectionId}/info")
    public Result getSectionData(@RequestHeader(value = "T-Token", required = false) String token, @PathVariable Integer sectionId, Integer page, Integer perPage) throws ParseException {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return sectionService.getSectionData(null, (long) sectionId, page, perPage);
        }
        Long userId = Long.valueOf(userIdStr);
        return sectionService.getSectionData(userId, (long) sectionId, page, perPage);
    }

    @GetMapping("{subsectionId}")
    public Result getPostsBySubsection(@PathVariable Integer subsectionId, Integer page, Integer perPage) {
        PostsWithTagDTO postsWithTagDTO = new PostsWithTagDTO((long) subsectionId, page, perPage);
        return sectionService.getPostsByTag(postsWithTagDTO);
    }

    @PostMapping("collect")
    public Result collectSection(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map) {
        Long sectionId = Long.valueOf(map.get("sectionId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return collectService.collectSection(userId, sectionId);
    }

    @PostMapping("publish_post")
    public Result publishPost(@RequestHeader("T-Token") String token, @RequestBody PostPublishDTO postPublishDTO) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return postService.publishPost(userId, postPublishDTO);
    }

    @GetMapping("{sectionId}/search")
    public Result getPostsBySearch(@PathVariable Integer sectionId, Integer page, Integer perPage, String content) {
        SectionSearchPostDTO sectionSearchPostDTO = new SectionSearchPostDTO((long) sectionId, page, perPage, content);
        if(Objects.equals(content, "") || Objects.equals(content,null)){
            SectionDataDTO sectionDataDTO = new SectionDataDTO((long) sectionId,page,perPage);
            return sectionService.getAllPostsInSection(sectionDataDTO);
        }
        return sectionService.getPostsInSectionByContent(sectionSearchPostDTO);
    }

    @GetMapping("{section_id}/post")
    public Result getAllPosts(@PathVariable Integer section_id, Integer page, Integer perPage) {
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long) section_id, page, perPage);
        return sectionService.getAllPostsInSection(sectionDataDTO);
    }

    @GetMapping("{sectionId}/pinned_post")
    public Result getPinnedPosts(@PathVariable Integer sectionId) {
        return sectionService.getPinnedPosts((long) sectionId);
    }

    @GetMapping("{sectionId}/highlighted_post")
    public Result getHighlightedPosts(@PathVariable Integer sectionId, Integer page, Integer perPage) {
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long) sectionId, page, perPage);
        return sectionService.getHighlightedPostsInSectionWithPage(sectionDataDTO);
    }

    @PostMapping("follow")
    public Result followSection(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map) {
        Long sectionId = Long.valueOf(map.get("sectionId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return collectService.collectSection(userId, sectionId);
    }
}
