package com.example.helloworld.entity;

import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result<T> {
    private int code;       // 状态码 200成功 500失败
    private String msg;     // 提示信息
    private T data;         // 返回数据

    // 添加有参构造函数
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功返回（自定义提示）
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // 失败返回
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}