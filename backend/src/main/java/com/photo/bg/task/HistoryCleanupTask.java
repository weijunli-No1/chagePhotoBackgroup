package com.photo.bg.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photo.bg.entity.PhotoHistory;
import com.photo.bg.mapper.PhotoHistoryMapper;
import com.photo.bg.service.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryCleanupTask {

    private final PhotoHistoryMapper photoHistoryMapper;
    private final MinioStorageService minioStorageService;

    /**
     * 每天晚上 12 点执行清理
     * cron: "0 0 0 * * ?"
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldHistory() {
        log.info("开始执行定时任务：清理 7 天前的历史证件照记录...");
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        LambdaQueryWrapper<PhotoHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(PhotoHistory::getCreateTime, sevenDaysAgo);
        
        List<PhotoHistory> oldRecords = photoHistoryMapper.selectList(queryWrapper);
        if (oldRecords != null && !oldRecords.isEmpty()) {
            for (PhotoHistory record : oldRecords) {
                // 删除 MinIO 中的物理文件
                minioStorageService.deleteFile(record.getPhotoUrl());
                minioStorageService.deleteFile(record.getOriginalPhotoUrl());
                
                // 真物理删除
                photoHistoryMapper.physicalDeleteById(record.getId());
            }
            log.info("成功清理了 {} 条历史记录及对应的照片文件。", oldRecords.size());
        } else {
            log.info("没有需要清理的旧历史记录。");
        }
    }
}