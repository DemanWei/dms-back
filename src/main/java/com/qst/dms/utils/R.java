package com.qst.dms.utils;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * 想要缓存Redis, 必须实现序列化接口
 * {"success":false,"obj":{},"info":"登录失败，用户名或密码错误"}
 * */
public class R<T> implements Serializable {
    private Boolean success;
    private String info;
    private T obj;

    public static <T> R<T> success(T obj) {
        R<T> r = new R<T>();
        r.obj = obj;
        r.success = true;
        r.info = "请求成功";
        return r;
    }

    public static <T> R<T> error(String info) {
        R r = new R();
        r.info = info;
        r.success = false;
        return r;
    }
}
