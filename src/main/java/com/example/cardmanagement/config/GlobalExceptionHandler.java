package com.example.cardmanagement.config;

import com.example.cardmanagement.util.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 用于统一处理系统中的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理所有异常
     * @param e 异常对象
     * @return Response 响应对象
     */
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        e.printStackTrace();
        return Response.error(500, "服务器内部错误: " + e.getMessage());
    }
    
    /**
     * 处理 IllegalArgumentException 异常
     * @param e 异常对象
     * @return Response 响应对象
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleIllegalArgumentException(IllegalArgumentException e) {
        return Response.error(400, "参数错误: " + e.getMessage());
    }
    
    /**
     * 处理 NullPointerException 异常
     * @param e 异常对象
     * @return Response 响应对象
     */
    @ExceptionHandler(NullPointerException.class)
    public Response handleNullPointerException(NullPointerException e) {
        return Response.error(500, "空指针异常: " + e.getMessage());
    }
}