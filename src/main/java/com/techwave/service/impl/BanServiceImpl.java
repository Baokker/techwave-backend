package com.techwave.service.impl;/**
 * @author baokker
 * @date 2023/4/28
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.SectionBanUser;
import com.techwave.mapper.SectionBanUserMapper;
import com.techwave.service.BanService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @descriptions: 在版块内封禁用户
 * @author: baokker
 * @date: 2023/4/28 16:08
 * @version: 1.0
 */
@Service
public class BanServiceImpl implements BanService {
    @Autowired
    SectionBanUserMapper sectionBanUserMapper;

    @Override
    public Result banSectionUser(Long userId, Long sectionId, Timestamp banUntil) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String banUntilString = format.format(banUntil);

        SectionBanUser sectionBanUser = SectionBanUser.builder()
                .userId(userId)
                .sectionId(sectionId)
                .banUntil(banUntilString)
                .build();

        LambdaQueryWrapper<SectionBanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SectionBanUser::getUserId, userId)
                .eq(SectionBanUser::getSectionId, sectionId);
        SectionBanUser sectionBanUser1 = sectionBanUserMapper.selectOne(queryWrapper);
        if (sectionBanUser1 != null) {
            sectionBanUserMapper.deleteById(sectionBanUser1.getId());
        }

        sectionBanUserMapper.insert(sectionBanUser);
        return Result.builder().code(TCode.SUCCESS.getCode()).msg("封禁成功").build();
    }

    @Override
    public Result unBanSectionUser(Long userId, Long sectionId) {
        LambdaQueryWrapper<SectionBanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SectionBanUser::getUserId, userId)
                .eq(SectionBanUser::getSectionId, sectionId);
        sectionBanUserMapper.delete(queryWrapper);
        return Result.builder().code(TCode.SUCCESS.getCode()).msg("解封成功").build();
    }

    @Override
    public Boolean getUserIsBannedInSection(Long userId, Long sectionId) throws ParseException {
        LambdaQueryWrapper<SectionBanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SectionBanUser::getUserId, userId)
                .eq(SectionBanUser::getSectionId, sectionId);
        SectionBanUser sectionBanUser = sectionBanUserMapper.selectOne(queryWrapper);
        if (sectionBanUser == null) {
            return false;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp banUntil = new Timestamp(format.parse(sectionBanUser.getBanUntil()).getTime());

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            Long diffInMillis = currentTimestamp.getTime() - banUntil.getTime();
            if (diffInMillis > 0) {
                return false;
            } else {
                sectionBanUserMapper.deleteById(sectionBanUser.getId());
                return true;
            }
        }
    }
}
