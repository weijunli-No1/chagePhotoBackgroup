package com.photo.bg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.bg.entity.PhotoHistory;
import com.photo.bg.mapper.PhotoHistoryMapper;
import com.photo.bg.service.PhotoHistoryService;
import org.springframework.stereotype.Service;

@Service
public class PhotoHistoryServiceImpl extends ServiceImpl<PhotoHistoryMapper, PhotoHistory> implements PhotoHistoryService {
}
