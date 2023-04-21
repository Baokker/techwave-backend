package com.techwave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techwave.entity.PostAndComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostAndCommentMapper extends BaseMapper<PostAndComment> {
}
