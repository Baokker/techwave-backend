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
    public Result collectSection(@RequestHeader("T-Token") String token){
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        if (userIdStr == null) {
            return Result.fail(TCode.OTHER_ERROR.getCode(), "从token中解析到到userId为空", null);
        }
        Integer userId = Integer.parseInt(userIdStr);
        return sectionService.collectSection(userId);
    }

    @GetMapping("section")
    public Result hotSection(){
        return sectionService.hotSection(5);
    }

    @GetMapping("search")
    public Result searchSection(String searchContent){
        return sectionService.searchSection(searchContent);
    }

    @GetMapping("post")
    public Result hotPost(){
        return postService.hotPost();
    }

    @GetMapping("news")
    public Result getNews(){
        return postService.getNews();
    }
}
