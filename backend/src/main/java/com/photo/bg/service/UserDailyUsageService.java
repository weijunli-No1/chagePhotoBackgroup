package com.photo.bg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.bg.entity.UserDailyUsage;

import java.time.LocalDate;

public interface UserDailyUsageService extends IService<UserDailyUsage> {
    
    UserDailyUsage getOrCreateUsage(Long userId, LocalDate date);

    boolean checkSaveConstraint(Long userId, LocalDate date);

    void recordSave(Long userId, LocalDate date);

    void recordAdView(Long userId, LocalDate date);
}
