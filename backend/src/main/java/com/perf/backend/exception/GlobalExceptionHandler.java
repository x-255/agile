package com.perf.backend.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.perf.backend.dto.Result;

/**
 * 全局异常处理器
 * 捕获所有Controller层抛出的异常，统一返回Result格式的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理所有未捕获的异常
     * @param e 异常对象
     * @return Result 错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 记录异常日志
        e.printStackTrace();
        
        // 返回错误响应
        return Result.fail(500, "服务器内部错误: " + e.getMessage());
    }
    
    /**
     * 处理业务异常
     * @param e 业务异常
     * @return Result 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }
}