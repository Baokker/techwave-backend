package com.techwave.service;

import com.techwave.utils.Result;

/**
 * @author baokker
 * @date 2023/4/28
 */
public interface FollowService {
    /**
     * 关注或取消关注
     * @param followerId 关注者id
     * @param followingId 被关注者id
     * @return
     */
    Result followOrUnfollow(Long followerId, Long followingId);

    Result isFollow(Long myUserId, Long userId);
}
