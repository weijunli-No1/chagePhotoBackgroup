package com.photo.bg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.bg.entity.UserDailyUsage;
import com.photo.bg.mapper.UserDailyUsageMapper;
import com.photo.bg.service.UserDailyUsageService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserDailyUsageServiceImpl extends ServiceImpl<UserDailyUsageMapper, UserDailyUsage> implements UserDailyUsageService {

    @Override
    public UserDailyUsage getOrCreateUsage(Long userId, LocalDate date) {
        QueryWrapper<UserDailyUsage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("record_date", date);
        UserDailyUsage usage = getOne(queryWrapper);
        if (usage == null) {
            usage = new UserDailyUsage();
            usage.setUserId(userId);
            usage.setRecordDate(date);
            usage.setSaveCount(0);
            usage.setAdViewCount(0);
            save(usage);
        }
        return usage;
    }

    @Override
    public boolean checkSaveConstraint(Long userId, LocalDate date) {
        UserDailyUsage usage = getOrCreateUsage(userId, date);
        if (usage.getAdViewCount() >= 5) {
            return true;
        }
        if (usage.getSaveCount() < 5 + usage.getAdViewCount()) {
            return true;
        }
        return false;
    }

    @Override
    public void recordSave(Long userId, LocalDate date) {
        UserDailyUsage usage = getOrCreateUsage(userId, date);
        usage.setSaveCount(usage.getSaveCount() + 1);
        updateById(usage);
    }

    @Override
    public void recordAdView(Long userId, LocalDate date) {
        UserDailyUsage usage = getOrCreateUsage(userId, date);
        usage.setAdViewCount(usage.getAdViewCount() + 1);
        updateById(usage);
    }
}