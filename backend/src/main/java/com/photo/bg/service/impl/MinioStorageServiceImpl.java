package com.photo.bg.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.photo.bg.config.MinioConfig;
import com.photo.bg.service.MinioStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MinIO 文件存储服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements MinioStorageService {

    private static final long MINIO_PART_SIZE = 10 * 1024 * 1024L;

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 初始化存储桶与读策略。
     */
    @PostConstruct
    public void init() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
                // 设置通用的读权限策略以便前端直接访问
                String policyJson = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + minioConfig.getBucketName() + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .config(policyJson)
                        .build());
                log.info("Bucket {} created successfully with public read policy.", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("Init MinIO Bucket failed!", e);
        }
    }

    /**
     * 上传 Base64 图片到 MinIO。
     */
    @Override
    public String uploadBase64(String base64, String extension) {
        if (StrUtil.isBlank(base64)) {
            throw new IllegalArgumentException("Base64 string cannot be empty");
        }
        
        // 去除可能的 "data:image/png;base64," 前缀
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }

        byte[] decode = Base64.decode(base64);
        ByteArrayInputStream bais = new ByteArrayInputStream(decode);
        
        String contentType = "image/png";
        if (".jpg".equalsIgnoreCase(extension) || ".jpeg".equalsIgnoreCase(extension)) {
            contentType = "image/jpeg";
        }
        
        String fileName = IdUtil.fastSimpleUUID() + extension;
        // 按日期分目录
        String objectName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + fileName;

        return uploadFile(bais, objectName, contentType);
    }

    /**
     * 上传文件流到 MinIO。
     */
    @Override
    public String uploadFile(InputStream inputStream, String objectName, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, -1, MINIO_PART_SIZE)
                            .contentType(contentType)
                            .build()
            );
            // 拼装访问 URL
            return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    /**
     * 根据访问 URL 删除 MinIO 文件。
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) return;
        try {
            // URL 格式一般为: endpoint/bucketName/objectName
            String prefix = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/";
            if (fileUrl.startsWith(prefix)) {
                String objectName = fileUrl.substring(prefix.length());
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(objectName)
                                .build()
                );
                log.info("Deleted file from MinIO: {}", objectName);
            }
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", fileUrl, e);
        }
    }
}
