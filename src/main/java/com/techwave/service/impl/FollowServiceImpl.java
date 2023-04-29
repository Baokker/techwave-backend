package com.techwave.service.impl;
/**
 * @author baokker
 * @date 2023/4/28
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.Follow;
import com.techwave.mapper.FollowMapper;
import com.techwave.service.FollowService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @descriptions: 关注
 * @author: baokker
 * @date: 2023/4/28 15:45
 * @version: 1.0
 */
@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private FollowMapper followMapper;

    @Override
    public Result followOrUnfollow(Long followerId, Long followingId) {
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId);
        Follow follow = followMapper.selectOne(queryWrapper);
        if (follow == null) {
            follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followMapper.insert(follow);
            return Result.success(TCode.SUCCESS.getCode(), "关注成功", null);
        } else {
            followMapper.deleteById(follow.getId());
            return Result.success(TCode.SUCCESS.getCode(), "取消关注成功", null);
        }
    }
}
