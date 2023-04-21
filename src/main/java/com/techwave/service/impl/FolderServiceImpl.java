package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.CollectionAndFolder;
import com.techwave.entity.dto.RenameFolderDTO;
import com.techwave.entity.vo.CollectPostsVO;
import com.techwave.entity.vo.FolderPostVO;
import com.techwave.entity.vo.FolderVO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.FolderPostDTO;
import com.techwave.mapper.CollectionAndFolderMapper;
import com.techwave.service.CollectService;
import com.techwave.service.FolderService;
import com.techwave.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wlf 1557177832@qq.com
 * @version 2022/12/8 23:07
 * @since JDK18
 */

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private CollectionAndFolderMapper collectionAndFolderMapper;
    @Autowired
    private CollectService collectService;
    @Autowired
    private PostService postService;
    @Override
    public Result createFolder(Long userId, String folderName) {
        LambdaQueryWrapper<CollectionAndFolder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndFolder::getUserId,userId);
        queryWrapper.eq(CollectionAndFolder::getName,folderName);
        queryWrapper.last("limit 1");
        CollectionAndFolder collectionAndFolder = collectionAndFolderMapper.selectOne(queryWrapper);
        if(collectionAndFolder !=null){
            return Result.fail(-1,"该文件夹名字重复",null);
        }
        else{
            CollectionAndFolder collectionAndFolder1 = new CollectionAndFolder();
            collectionAndFolder1.setName(folderName);
            collectionAndFolder1.setUserId(userId);
            collectionAndFolderMapper.insert(collectionAndFolder1);
            return Result.success(20000,"okk",null);
        }
    }

    @Override
    public Result renameFolder(RenameFolderDTO renameFolderDTO) {
        Long folderId = renameFolderDTO.getFolderId();
        String folderName = renameFolderDTO.getFolderName();

        LambdaQueryWrapper<CollectionAndFolder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndFolder::getId,folderId);
        queryWrapper.eq(CollectionAndFolder::getName,folderName);
        queryWrapper.last("limit 1");
        CollectionAndFolder collectionAndFolder = collectionAndFolderMapper.selectOne(queryWrapper);
        if(collectionAndFolder ==null){
            CollectionAndFolder collectionAndFolderById = this.findFolderById(folderId);
            collectionAndFolderById.setName(folderName);
            collectionAndFolderMapper.updateById(collectionAndFolderById);
            return Result.success(20000,"okk",null);
        }
        else{
            return Result.fail(-1,"已存在该名字的文件夹",null);
        }
    }

    @Override
    public Result getFolders(Long userId) {
        LambdaQueryWrapper<CollectionAndFolder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndFolder::getUserId,userId);
        List<CollectionAndFolder> collectionAndFolders = collectionAndFolderMapper.selectList(queryWrapper);
        List<FolderVO> folderVOList = copyList(collectionAndFolders);
        Map<String,Object> map = new HashMap<>();
        map.put("folders",folderVOList);
        return Result.success(map);
    }

    @Override
    public Result getCollectInfo(Long userId, FolderPostDTO folderPostDTO) {
        Long folderId = folderPostDTO.getFolderId();
        Integer curPage = folderPostDTO.getCurPage();
        Integer limit = folderPostDTO.getLimit();
        if(folderId==null||curPage==null||limit==null){
            return Result.fail(-1,"参数有误",null);
        }
        if(folderId==0){
            LambdaQueryWrapper<CollectionAndFolder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CollectionAndFolder::getUserId,userId);
            queryWrapper.orderByAsc(CollectionAndFolder::getId);
            queryWrapper.last("limit 1");
            CollectionAndFolder collectionAndFolder = collectionAndFolderMapper.selectOne(queryWrapper);
            folderId = collectionAndFolder.getId();
        }

        List<FolderPostVO> folderPostVOList = postService.findPostsByFolderIdWithPage(folderId,curPage,limit);
        CollectPostsVO collectPostsVO = new CollectPostsVO();
        collectPostsVO.setFolderPostDTOList(folderPostVOList);
        if(folderPostVOList!=null){
            collectPostsVO.setTotal(folderPostVOList.size());
        }
        else{
            collectPostsVO.setTotal(0);
        }



        return Result.success(20000,"okk",collectPostsVO);
    }

    @Override
    public Result deleteFolder(Long userId, Long folderId) {
        LambdaQueryWrapper<CollectionAndFolder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndFolder::getId,folderId);
        queryWrapper.last("limit 1");
        CollectionAndFolder collectionAndFolder = collectionAndFolderMapper.selectOne(queryWrapper);
        if(collectionAndFolder ==null){
            return Result.fail(-1,"参数有误",null);
        }
        collectionAndFolderMapper.deleteById(collectionAndFolder);

        collectService.deleteCollectPostByFolderId(folderId);

        return Result.success(20000,"操作成功",null);
    }

    private List<FolderVO> copyList(List<CollectionAndFolder> collectionAndFolders) {
        List<FolderVO> folderVOList = new ArrayList<>();
        for (CollectionAndFolder collectionAndFolder :
                collectionAndFolders) {
            folderVOList.add(copy(collectionAndFolder));
        }
        return folderVOList;
    }

    private FolderVO copy(CollectionAndFolder collectionAndFolder) {
        FolderVO folderVO = new FolderVO();
        BeanUtils.copyProperties(collectionAndFolder,folderVO);
        return folderVO;
    }

    private CollectionAndFolder findFolderById(Long folderId) {
        LambdaQueryWrapper<CollectionAndFolder> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(CollectionAndFolder::getId,folderId);
        queryWrapper.last("limit 1");
        CollectionAndFolder collectionAndFolder = collectionAndFolderMapper.selectOne(queryWrapper);
        return collectionAndFolder;
    }
}
