package com.techwave.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.Post;
import com.techwave.entity.Section;
import com.techwave.entity.User;
import com.techwave.mapper.PostMapper;
import com.techwave.mapper.SectionMapper;
import com.techwave.mapper.UserMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 线程方面的服务
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/7 20:46
 * @since JDK18
 */
@Component
public class ThreadService {

    @Async("threadpool")
    public void updateViewCount(PostMapper postMapper, Post post) {
        Post updatePost = new Post();
        updatePost.setViewCount(post.getViewCount()+1);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId,post.getId());
        queryWrapper.eq(Post::getViewCount,post.getViewCount());
        postMapper.update(updatePost,queryWrapper);
    }


    @Async("threadpool")
    public void updateSectionByCollectCount(SectionMapper sectionMapper, Section section, Boolean b) {
        Section updateSection = new Section();
        if(b){
            updateSection.setUserCount(section.getUserCount()+1);
        }
        else{
            updateSection.setUserCount(section.getUserCount()-1);
        }
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getId,section.getId());
        queryWrapper.eq(Section::getUserCount,section.getUserCount());
        sectionMapper.update(updateSection,queryWrapper);
    }
    @Async("threadpool")
    public void updateSectionByPostCount(SectionMapper sectionMapper, Section section, boolean b) {
        Section updateSection = new Section();
        if(b){
            updateSection.setPostCount(section.getPostCount()+1);
        }
        else{
            updateSection.setPostCount(section.getPostCount()-1);
        }
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getId,section.getId());
        queryWrapper.eq(Section::getPostCount,section.getPostCount());
        sectionMapper.update(updateSection,queryWrapper);
    }
    @Async("threadpool")
    public void updatePostByCommentCount(PostMapper postMapper, Post post, boolean b) {
        Post updatePost = new Post();
        if(b){
            updatePost.setCommentCount(post.getCommentCount()+1);
        }else{
            updatePost.setCommentCount(post.getCommentCount()-1);
        }
        LambdaQueryWrapper<Post> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId,post.getId());
        queryWrapper.eq(Post::getCommentCount,post.getCommentCount());
        postMapper.update(updatePost,queryWrapper);
    }

    @Async("threadpool")
    public void updateUserByAvatar(UserMapper userMapper, User user) {
        userMapper.updateById(user);
    }
}
