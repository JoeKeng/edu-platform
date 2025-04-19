package com.edusystem.exception;

import com.edusystem.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有运行时异常
     */
   @ExceptionHandler(RuntimeException.class)
   public Result handleRuntimeException(RuntimeException ex) {
       log.error("发生异常: {}", ex.getMessage());
       return Result.error(ex.getMessage()); // 返回错误信息
   }
}
