package com.photo.bg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.bg.entity.User;

/**
 * 用户服务。
 */
public interface UserService extends IService<User> {

    /**
     * 根据微信登录 code 执行登录或注册。
     *
     * @param code 微信登录临时凭证
     * @return 用户实体
     */
    User loginOrRegister(String code);
}
