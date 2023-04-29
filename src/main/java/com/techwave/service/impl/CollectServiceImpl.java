package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.CollectionAndPost;
import com.techwave.entity.CollectionAndSection;
import com.techwave.entity.dto.CollectPostDTO;
import com.techwave.utils.TCode;
import com.techwave.utils.Result;
import com.techwave.mapper.CollectionAndPostMapper;
import com.techwave.mapper.CollectAndSectionMapper;
import com.techwave.service.CollectService;
import com.techwave.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏功能的实现类
 *
 * @author wlf 1557177832@qq.com
 * @version 2022/12/3 15:21
 * @since JDK18
 */
@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private CollectionAndPostMapper collectAndPostMapper;
    @Autowired
    private CollectAndSectionMapper collectAndSectionMapper;


    @Override
    public Boolean isUserCollectPost(Long userId, Long postId) {
        LambdaQueryWrapper<CollectionAndPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndPost::getPostId, postId);
        queryWrapper.eq(CollectionAndPost::getUserId, userId);
        queryWrapper.last("limit 1");
        if (collectAndPostMapper.selectOne(queryWrapper) == null)
            return false;
        else
            return true;
    }

    @Override
    public Boolean isUserCollectSection(Long userId, Long sectionId) {
        LambdaQueryWrapper<CollectionAndSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndSection::getSectionId, sectionId);
        queryWrapper.eq(CollectionAndSection::getUserId, userId);
        queryWrapper.last("limit 1");
        if (collectAndSectionMapper.selectOne(queryWrapper) == null)
            return false;
        else
            return true;
    }

    @Override
    public Result collectSection(Long userId, Long sectionId) {
        Boolean isCollected = this.isUserCollectSection(userId, sectionId);
        LambdaQueryWrapper<CollectionAndSection> queryWrapper = new LambdaQueryWrapper<>();
        if (isCollected) {
            queryWrapper.eq(CollectionAndSection::getUserId, userId);
            queryWrapper.eq(CollectionAndSection::getSectionId, sectionId);
            queryWrapper.last("limit 1");
            this.collectAndSectionMapper.delete(queryWrapper);

            sectionService.updateSectionByCollectCount(sectionId, false);
            return Result.success(TCode.SUCCESS.getCode(), "已取消关注版块", null);
        } else {
            CollectionAndSection collectionAndSection = new CollectionAndSection();
            collectionAndSection.setSectionId(sectionId);
            collectionAndSection.setUserId(userId);
            this.collectAndSectionMapper.insert(collectionAndSection);

            sectionService.updateSectionByCollectCount(sectionId, true);
            return Result.success(20000, "关注版块成功", null);
        }
    }

    @Override
    public Result collectPost(Long userId, CollectPostDTO collectPostDTO) {
        Long postId = collectPostDTO.getId();
        Long folderId = collectPostDTO.getFolderId();
        if (postId == null && folderId == null) {
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg());
        }

        LambdaQueryWrapper<CollectionAndPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndPost::getPostId, postId);
        queryWrapper.eq(CollectionAndPost::getUserId, userId);
        queryWrapper.last("limit 1");
        CollectionAndPost collectionAndPost1 = collectAndPostMapper.selectOne(queryWrapper);

        if (collectionAndPost1 == null && folderId == null) {
            return Result.fail(-1, "参数有误", null);
        }

        if (collectionAndPost1 == null) {
            CollectionAndPost collectionAndPost = new CollectionAndPost();
            collectionAndPost.setPostId(postId);
            collectionAndPost.setUserId(userId);
            collectionAndPost.setFolderId(folderId);
            collectAndPostMapper.insert(collectionAndPost);
            return Result.success(20000, "收藏成功", null);
        } else {
            collectAndPostMapper.delete(queryWrapper);
            return Result.success(20000, "取消收藏成功", null);
        }
    }

    @Override
    public List<Long> findPostIdsByFolderId(Long folderId) {
        LambdaQueryWrapper<CollectionAndPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndPost::getFolderId, folderId);
        List<CollectionAndPost> collectionAndPosts = collectAndPostMapper.selectList(queryWrapper);
        if (collectionAndPosts.size() == 0) {
            return null;
        }
        List<Long> postIds = new ArrayList<>();
        for (CollectionAndPost collectionAndPost :
                collectionAndPosts) {
            postIds.add(collectionAndPost.getPostId());
        }
        return postIds;
    }

    @Override
    public void deleteCollectPostByFolderId(Long folderId) {
        LambdaQueryWrapper<CollectionAndPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndPost::getFolderId, folderId);
        collectAndPostMapper.delete(queryWrapper);
    }
}
