package com.photo.bg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.bg.entity.UserDailyUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

@Mapper
public interface UserDailyUsageMapper extends BaseMapper<UserDailyUsage> {

    @Update("UPDATE t_user_daily_usage SET save_count = save_count + 1 WHERE user_id = #{userId} AND record_date = #{recordDate}")
    int incrementSaveCount(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    @Update("UPDATE t_user_daily_usage SET ad_view_count = ad_view_count + 1 WHERE user_id = #{userId} AND record_date = #{recordDate}")
    int incrementAdViewCount(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);
}
