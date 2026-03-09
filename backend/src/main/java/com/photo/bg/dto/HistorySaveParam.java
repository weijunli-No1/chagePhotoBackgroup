package com.photo.bg.dto;

import lombok.Data;

@Data
public class HistorySaveParam {
    private String openid; // 由于尚未实现JWT，暂时靠openid区分用户，真实环境建议换成 token 解析
    private String base64Image;
    private String bgColor;
    // 如果有原图存下来的话，也可以在这里传
    private String originalPhotoUrl;
}
