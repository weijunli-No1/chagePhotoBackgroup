package com.photo.bg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.photo.bg.common.Result;
import com.photo.bg.entity.User;
import com.photo.bg.entity.UserDailyUsage;
import com.photo.bg.service.UserDailyUsageService;
import com.photo.bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDailyUsageService userDailyUsageService;

    @PostMapping("/login")
    public Result<User> login(@RequestParam String code) {
        User user = userService.loginOrRegister(code);
        return Result.success(user);
    }
    
    @PostMapping("/recordAd")
    public Result<Void> recordAd(@RequestParam String openid) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            return Result.error(401, "用户未登录");
        }
        userDailyUsageService.recordAdView(user.getId(), LocalDate.now());
        return Result.success(null);
    }
    
    @GetMapping("/usage")
    public Result<UserDailyUsage> getUsage(@RequestParam String openid) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            return Result.error(401, "用户未登录");
        }
        UserDailyUsage usage = userDailyUsageService.getOrCreateUsage(user.getId(), LocalDate.now());
        return Result.success(usage);
    }
}
