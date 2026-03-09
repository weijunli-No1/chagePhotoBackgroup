package com.photo.bg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.bg.entity.User;

public interface UserService extends IService<User> {
    User loginOrRegister(String code);
}
