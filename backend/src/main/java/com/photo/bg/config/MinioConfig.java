package com.photo.bg.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** MinIO 服务端点。 */
    private String endpoint;
    /** MinIO AccessKey。 */
    private String accessKey;
    /** MinIO SecretKey。 */
    private String secretKey;
    /** 存储桶名称。 */
    private String bucketName;

    /**
     * 构建 MinIO 客户端。
     *
     * @return MinIO 客户端实例
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
