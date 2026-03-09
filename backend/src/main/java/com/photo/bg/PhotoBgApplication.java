package com.photo.bg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.photo.bg.mapper")
public class PhotoBgApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhotoBgApplication.class, args);
    }
}
