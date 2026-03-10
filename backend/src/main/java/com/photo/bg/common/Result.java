package com.photo.bg.common;

import lombok.Data;

/**
 * 统一返回对象。
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> {

    private static final Integer SUCCESS_CODE = 200;
    private static final String SUCCESS_MESSAGE = "操作成功";

    private Integer code;
    private String message;
    private T data;

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage(SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    /**
     * 构造无数据的成功响应。
     *
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 构造失败响应。
     *
     * @param code 错误码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
