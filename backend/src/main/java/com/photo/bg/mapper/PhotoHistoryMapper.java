package com.photo.bg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.photo.bg.entity.PhotoHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PhotoHistoryMapper extends BaseMapper<PhotoHistory> {

    @Delete("DELETE FROM t_photo_history WHERE id = #{id}")
    void physicalDeleteById(@Param("id") Long id);
}
