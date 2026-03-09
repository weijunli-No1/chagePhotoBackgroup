package com.photo.bg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photo.bg.common.Result;
import com.photo.bg.dto.HistorySaveParam;
import com.photo.bg.entity.PhotoHistory;
import com.photo.bg.entity.User;
import com.photo.bg.service.MinioStorageService;
import com.photo.bg.service.PhotoHistoryService;
import com.photo.bg.service.UserDailyUsageService;
import com.photo.bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class PhotoHistoryController {

    private final PhotoHistoryService photoHistoryService;
    private final MinioStorageService minioStorageService;
    private final UserService userService;
    private final UserDailyUsageService userDailyUsageService;

    @PostMapping("/save")
    public Result<PhotoHistory> saveHistory(@RequestBody HistorySaveParam param) {
        // 1. 查找用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, param.getOpenid()));
        if (user == null) {
            return Result.error(401, "用户未登录");
        }

        // Check save constraint
        LocalDate today = LocalDate.now();
        boolean canSave = userDailyUsageService.checkSaveConstraint(user.getId(), today);
        if (!canSave) {
            return Result.error(4031, "今日免费保存次数已达上限，请观看广告后继续保存");
        }

        // 2. 将base64图片上传到MinIO
        String photoUrl = minioStorageService.uploadBase64(param.getBase64Image(), ".png");

        // 3. 落库记录
        PhotoHistory history = new PhotoHistory();
        history.setUserId(user.getId());
        history.setPhotoUrl(photoUrl);
        history.setBgColor(param.getBgColor());
        history.setOriginalPhotoUrl(param.getOriginalPhotoUrl());
        history.setCreateTime(LocalDateTime.now());
        history.setIsDeleted(0);
        
        photoHistoryService.save(history);
        
        // record save usage
        userDailyUsageService.recordSave(user.getId(), today);

        return Result.success(history);
    }

    @GetMapping("/list")
    public Result<List<PhotoHistory>> listHistory(@RequestParam String openid) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            return Result.error(401, "用户未登录");
        }

        List<PhotoHistory> list = photoHistoryService.list(
                new LambdaQueryWrapper<PhotoHistory>()
                        .eq(PhotoHistory::getUserId, user.getId())
                        .orderByDesc(PhotoHistory::getCreateTime)
        );

        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteHistory(@PathVariable Long id) {
        boolean removed = photoHistoryService.removeById(id);
        return Result.success(removed);
    }
}
