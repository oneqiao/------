package com.example.cardmanagement.util;

import java.util.Date;

/**
 * 通用响应类
 * 用于统一API接口的响应格式
 */
public class Response {

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 时间戳
     */
    private long timestamp;

    public Response() {
        this.timestamp = new Date().getTime();
    }

    public Response(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = new Date().getTime();
    }

    public static Response success() {
        return new Response(200, "success", null);
    }

    public static Response success(Object data) {
        return new Response(200, "success", data);
    }

    public static Response success(String message) {
        return new Response(200, message, null);
    }

    public static Response success(String message, Object data) {
        return new Response(200, message, data);
    }

    public static Response error(int code, String message) {
        return new Response(code, message, null);
    }

    public static Response error(String message) {
        return new Response(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}