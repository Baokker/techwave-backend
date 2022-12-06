package com.tjsse.jikespace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tjsse.jikespace.entity.CollectAndPost;
import com.tjsse.jikespace.entity.CollectAndSection;
import com.tjsse.jikespace.entity.Section;
import com.tjsse.jikespace.mapper.CollectAndPostMapper;
import com.tjsse.jikespace.mapper.CollectAndSectionMapper;
import com.tjsse.jikespace.mapper.SectionMapper;
import com.tjsse.jikespace.service.CollectService;
import com.tjsse.jikespace.utils.JKCode;
import com.tjsse.jikespace.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private CollectAndPostMapper collectAndPostMapper;
    @Autowired
    private CollectAndSectionMapper collectAndSectionMapper;
    @Autowired
    private SectionMapper sectionMapper;


    @Override
    public Boolean isUserCollectPost(Long userId, Long postId) {
        LambdaQueryWrapper<CollectAndPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectAndPost::getId,postId);
        queryWrapper.eq(CollectAndPost::getUserId,userId);
        queryWrapper.last("limit 1");
        if(collectAndPostMapper.selectOne(queryWrapper)==null)
            return false;
        else
            return true;
    }

    @Override
    public Boolean isUserCollectSection(Long userId, Long sectionId) {
        LambdaQueryWrapper<CollectAndSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectAndSection::getId,sectionId);
        queryWrapper.eq(CollectAndSection::getUserId,userId);
        queryWrapper.last("limit 1");
        if(collectAndSectionMapper.selectOne(queryWrapper)==null)
            return false;
        else
            return true;
    }

    @Override
    public Result collectSection(Long userId, Long sectionId) {
        Boolean isCollected = this.isUserCollectSection(userId,sectionId);
        LambdaQueryWrapper<CollectAndSection> queryWrapper= new LambdaQueryWrapper<>();
        if(isCollected){
            queryWrapper.eq(CollectAndSection::getUserId,userId);
            queryWrapper.eq(CollectAndSection::getSectionId,sectionId);
            queryWrapper.last("limit 1");
            this.collectAndSectionMapper.delete(queryWrapper);

            LambdaQueryWrapper<Section> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Section::getId,sectionId);
            queryWrapper1.last("limit 1");
            Section section = sectionMapper.selectOne(queryWrapper1);
            section.setPostCounts(section.getPostCounts()-1);
            sectionMapper.updateById(section);
            return Result.success(JKCode.SUCCESS.getCode(),"已取消收藏");
        }
        else {
            CollectAndSection collectAndSection = new CollectAndSection();
            collectAndSection.setSectionId(sectionId);
            collectAndSection.setUserId(userId);
            this.collectAndSectionMapper.insert(collectAndSection);

            LambdaQueryWrapper<Section> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Section::getId,sectionId);
            queryWrapper2.last("limit 1");
            Section section = sectionMapper.selectOne(queryWrapper2);
            section.setPostCounts(section.getPostCounts()+1);
            sectionMapper.updateById(section);
            return Result.success(20000,"收藏成功");
        }
    }
}
