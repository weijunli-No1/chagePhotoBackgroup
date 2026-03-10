package com.photo.bg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.bg.entity.UserDailyUsage;

import java.time.LocalDate;

public interface UserDailyUsageService extends IService<UserDailyUsage> {

    /**
     * 查询指定用户在指定日期的使用记录，不存在则创建。
     *
     * @param userId 用户 ID
     * @param date 日期
     * @return 使用记录
     */
    UserDailyUsage getOrCreateUsage(Long userId, LocalDate date);

    /**
     * 校验用户是否仍可继续保存照片。
     *
     * @param userId 用户 ID
     * @param date 日期
     * @return true-可保存，false-不可保存
     */
    boolean checkSaveConstraint(Long userId, LocalDate date);

    /**
     * 记录一次保存行为。
     *
     * @param userId 用户 ID
     * @param date 日期
     */
    void recordSave(Long userId, LocalDate date);

    /**
     * 记录一次广告观看行为。
     *
     * @param userId 用户 ID
     * @param date 日期
     */
    void recordAdView(Long userId, LocalDate date);
}
