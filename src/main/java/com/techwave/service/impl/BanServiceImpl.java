package com.techwave.service.impl;/**
 * @author baokker
 * @date 2023/4/28
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techwave.entity.Notification;
import com.techwave.entity.SectionBanUser;
import com.techwave.entity.dto.SectionBanUserDTO;
import com.techwave.entity.vo.SectionBanVO;
import com.techwave.mapper.SectionBanUserMapper;
import com.techwave.mapper.SectionMapper;
import com.techwave.mapper.UserMapper;
import com.techwave.service.BanService;
import com.techwave.service.UserService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    SectionMapper sectionMapper;

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
                sectionBanUserMapper.deleteById(sectionBanUser.getId());
                return true;
            } else {
                return false;
            }
        }
    }
    @Override
        public Result getBannedList(Integer sectionId) throws ParseException {
        List<SectionBanUser> banList = sectionBanUserMapper.selectList();
        for(SectionBanUser item:banList){
            Long userId = item.getUserId();
            this.getUserIsBannedInSection(userId,(long)sectionId);
        }
        List<SectionBanVO> newVo = new ArrayList<SectionBanVO>();
        for(SectionBanUser item:banList) {
            SectionBanVO sectionBanVO = new SectionBanVO();
            sectionBanVO.setSectionId(item.getSectionId());
            sectionBanVO.setBanUntil(item.getBanUntil());
            sectionBanVO.setUserId(item.getUserId());
            sectionBanVO.setId(item.getId());
            sectionBanVO.setCreatedAt(item.getCreatedAt());
            sectionBanVO.setUserName(userMapper.selectById(item.getUserId()).getUsername());
            if(item.getSectionId().longValue()==sectionId)
            {
                newVo.add(sectionBanVO);
            }
        }
        return Result.success(20000, "获取举报列表成功", newVo);
    }

    @Override
    public Result banUser(SectionBanUserDTO sectionBanUserDTO) {
        Long userId = Long.valueOf(sectionBanUserDTO.getTargetId());
        Long sectionId = Long.valueOf(sectionBanUserDTO.getSectionId());
        String duration= sectionBanUserDTO.getBanUntil();
        int banUntil=0;

        if(userId<0||sectionId<0||duration==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        switch (duration){
            case "一个月":
                banUntil = 30;
                break;
            case "三个月":
                banUntil = 90;
                break;
            case "半年":
                banUntil = 180;
                break;
            case "一年":
                banUntil = 365;
                break;
            default:break;
        }

        Timestamp banUntilTime = new Timestamp(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(banUntil));

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType("system");
        notification.setContent("在"+sectionMapper.selectById(sectionId).getName()+"版块中你被封禁至"+banUntilTime);
        notification.setIsRead(false);

        return banSectionUser(userId,sectionId,banUntilTime);
    }
}
