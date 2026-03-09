package com.photo.bg.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.photo.bg.service.PythonApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PythonApiServiceImpl implements PythonApiService {

    @Value("${python-api.base-url}")
    private String baseUrl;

    @Override
    public Map<String, Object> generateIdPhoto(MultipartFile file, Integer height, Integer width) {
        String url = baseUrl + "/idphoto";
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            
            HttpRequest request = HttpRequest.post(url)
                    .form("input_image", bytes, file.getOriginalFilename())
                    .form("hd", "true");
                    
            if (height != null) {
                request.form("height", height);
            }
            if (width != null) {
                request.form("width", width);
            }
            
            HttpResponse response = request.execute();
                    
            if (response.isOk()) {
                return JSON.parseObject(response.body(), Map.class);
            } else {
                log.error("调用Python接口 idphoto 失败: {}", response.body());
                throw new RuntimeException("处理图像失败");
            }
        } catch (Exception e) {
            log.error("生成证件照异常", e);
            throw new RuntimeException("生成证件照异常", e);
        }
    }

    @Override
    public Map<String, Object> addBackground(String base64Image, String color) {
        String url = baseUrl + "/add_background";
        try {
            HttpResponse response = HttpRequest.post(url)
                    .form("input_image_base64", base64Image)
                    .form("color", color.replace("#", "")) // Python端可能不需要#
                    .execute();
                    
            if (response.isOk()) {
                return JSON.parseObject(response.body(), Map.class);
            } else {
                log.error("调用Python接口 add_background 失败: {}", response.body());
                throw new RuntimeException("处理图像失败");
            }
        } catch (Exception e) {
            log.error("添加背景色异常", e);
            throw new RuntimeException("添加背景色异常", e);
        }
    }

    @Override
    public Map<String, Object> generateLayoutPhotos(String base64Image, Integer height, Integer width) {
        String url = baseUrl + "/generate_layout_photos";
        try {
            HttpRequest request = HttpRequest.post(url)
                    .form("input_image_base64", base64Image);
            
            if (height != null) {
                request.form("height", height);
            }
            if (width != null) {
                request.form("width", width);
            }
            
            HttpResponse response = request.execute();
            
            if (response.isOk()) {
                return JSON.parseObject(response.body(), Map.class);
            } else {
                log.error("调用Python接口 generate_layout_photos 失败: {}", response.body());
                throw new RuntimeException("处理排版图像失败");
            }
        } catch (Exception e) {
            log.error("生成排版照异常", e);
            throw new RuntimeException("生成排版照异常", e);
        }
    }
}
