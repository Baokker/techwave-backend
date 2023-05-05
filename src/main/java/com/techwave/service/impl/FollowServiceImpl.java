package com.techwave.service.impl;
/**
 * @author baokker
 * @date 2023/4/28
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.Follow;
import com.techwave.mapper.FollowMapper;
import com.techwave.mapper.UserMapper;
import com.techwave.service.FollowService;
import com.techwave.service.UserService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result followOrUnfollow(Long followerId, Long followingId) {
        if (Objects.equals(followerId, followingId)) {
            return Result.success(TCode.FAIL.getCode(), "不能关注自己", false);
        }
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId);
        Follow follow = followMapper.selectOne(queryWrapper);
        if (follow == null) {
            follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followMapper.insert(follow);
            userMapper.addFollowCount(followerId);
            userMapper.addFanCount(followingId);
            return Result.success(TCode.SUCCESS.getCode(), "关注成功", null);
        } else {
            followMapper.deleteById(follow.getId());
            userMapper.subFollowCount(followerId);
            userMapper.subFanCount(followingId);
            return Result.success(TCode.SUCCESS.getCode(), "取消关注成功", null);
        }
    }

    @Override
    public Result isFollow(Long myUserId, Long userId) {
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, myUserId)
                .eq(Follow::getFollowingId, userId);
        Follow follow = followMapper.selectOne(queryWrapper);
        if (follow == null) {
            return Result.success(TCode.SUCCESS.getCode(), "未关注", false);
        } else {
            return Result.success(TCode.SUCCESS.getCode(), "已关注", true);
        }
    }
}
