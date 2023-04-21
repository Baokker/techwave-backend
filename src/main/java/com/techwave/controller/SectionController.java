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

import java.util.Map;

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

    @GetMapping("data/{{section_id}}")
    public Result getSectionData(@RequestHeader(value = "JK-Token", required = false) String token, @PathVariable Integer section_id, Integer page, Integer perPage){
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)section_id,page,perPage);
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return sectionService.getSectionData(null,sectionDataDTO);
        }
        Long userId = Long.valueOf(userIdStr);
        return sectionService.getSectionData(userId,sectionDataDTO);
    }

    @GetMapping("{{subsection_id}}")
    public Result getPostsBySubsection(Integer sectionId,@PathVariable Integer subsection_id,Integer page,Integer perPage){
        PostsWithTagDTO postsWithTagDTO = new PostsWithTagDTO((long)sectionId,(long)subsection_id,page,perPage);
        return sectionService.getPostsByTag(postsWithTagDTO);
    }

    @PostMapping ("collect")
    public Result collectSection(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map){
        Long sectionId = Long.valueOf(map.get("sectionId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return collectService.collectSection(userId,sectionId);
    }

    @PostMapping("publish_post")
    public Result publishPost(@RequestHeader("T-Token") String token, @RequestBody PostPublishDTO postPublishDTO){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return postService.publishPost(userId,postPublishDTO);
    }

    @GetMapping("{{section_id}}/search")
    public Result getPostsBySearch(@PathVariable Integer section_id,Integer page,Integer perPage,String content){
        SectionSearchPostDTO sectionSearchPostDTO = new SectionSearchPostDTO((long)section_id,page,perPage,content);
        return null;
    }

    @GetMapping("{{section_id}}/post")
    public Result getAllPosts(@PathVariable Integer section_id,Integer page,Integer perPage){
        SectionDataDTO sectionDataDTO = new SectionDataDTO((long)section_id,page,perPage);
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

    @PostMapping ("follow")
    public Result followSection(@RequestHeader("T-Token") String token, @RequestBody Map<String, String> map){
        Long sectionId = Long.valueOf(map.get("sectionId"));
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.valueOf(userIdStr);
        return null;
    }



}
