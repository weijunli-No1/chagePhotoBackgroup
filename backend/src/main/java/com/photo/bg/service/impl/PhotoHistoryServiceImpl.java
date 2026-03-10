package com.photo.bg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.bg.entity.PhotoHistory;
import com.photo.bg.mapper.PhotoHistoryMapper;
import com.photo.bg.service.PhotoHistoryService;
import org.springframework.stereotype.Service;

/**
 * 证件照历史记录服务实现。
 */
@Service
public class PhotoHistoryServiceImpl extends ServiceImpl<PhotoHistoryMapper, PhotoHistory> implements PhotoHistoryService {
}
