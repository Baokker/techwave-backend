package com.techwave.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techwave.entity.SectionBanUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SectionBanUserMapper extends BaseMapper<SectionBanUser> {
    List<SectionBanUser> selectList();
}
