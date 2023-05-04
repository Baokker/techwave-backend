package com.techwave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techwave.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: JiKeSpace
 * @description: mapper for user entity
 * @packagename: com.tjsse.jikespace.mapper
 * @author: peng peng
 * @date: 2022-11-29 18:08
 **/

@Mapper
public interface UserMapper extends BaseMapper<User> {
    void addFollowCount(Long followerId);

    void addFanCount(Long followingId);

    void subFollowCount(Long followerId);

    void subFanCount(Long followingId);
}
