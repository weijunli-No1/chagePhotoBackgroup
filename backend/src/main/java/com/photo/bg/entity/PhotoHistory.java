package com.photo.bg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_photo_history")
public class PhotoHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String photoUrl;
    private String originalPhotoUrl;
    private String bgColor;
    
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer isDeleted;
}
