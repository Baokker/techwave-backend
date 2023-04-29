package com.techwave.controller;

import com.techwave.utils.TCode;
import com.techwave.utils.JwtUtil;
import com.techwave.utils.Result;
import com.techwave.service.PostService;
import com.techwave.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 首页管理
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/6 20:46
 * @since JDK18
 */
@RestController
@RequestMapping("homepage/")
public class HomePageController {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private PostService postService;

    @GetMapping("collect")
    public Result collectSection(@RequestHeader("T-Token") String token, @RequestParam Integer page, @RequestParam Integer perPage) {
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Long userId = Long.parseLong(userIdStr);
        return sectionService.collectSection(userId, page, perPage);
    }

    @GetMapping("section")
    public Result hotSection(@RequestParam Integer page, @RequestParam Integer perPage) {
        return sectionService.hotSection(page, perPage);
    }

    @GetMapping("search")
    public Result searchSection(@RequestParam Integer page, @RequestParam Integer perPage, @RequestParam String content) {
        return sectionService.searchSection(page, perPage, content);
    }

    @GetMapping("post")
    public Result hotPost() {
        return postService.hotPost();
    }

    @GetMapping("news")
    public Result getNews() {
        return postService.getNews();
    }
}
