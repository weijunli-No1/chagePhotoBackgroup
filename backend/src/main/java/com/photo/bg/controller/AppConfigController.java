package com.photo.bg.controller;

import com.photo.bg.common.Result;
import com.photo.bg.config.WxMaConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppConfigController {

    private final WxMaConfiguration wxMaConfiguration;

    @GetMapping("/config")
    public Result<Map<String, Object>> getAppConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("rewardedVideoAdUnitId", wxMaConfiguration.getRewardedAdUnitId());
        return Result.success(config);
    }
}
