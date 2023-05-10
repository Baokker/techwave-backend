package com.techwave.service;

import com.techwave.utils.Result;

import java.sql.Timestamp;
import java.text.ParseException;

/**
 * @author baokker
 * @date 2023/4/28
 */
public interface BanService {
    // TODO 获取分页的封禁信息时，需要考虑数据库里的封禁用户可能解封的情况，
    // 例如，用户在28号就已经解封，但是29号获取时，仍然存在db中，需要进行判断
    Result banSectionUser(Long userId, Long sectionId, Timestamp banUntil);

    Result unBanSectionUser(Long userId, Long sectionId);

    Boolean getUserIsBannedInSection(Long userId, Long sectionId) throws ParseException;

    Result getBannedList(Integer sectionId);
}
