package com.modive.authservice.exception;

import com.modive.authservice.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleCustomException(CustomException e) {
        return new ApiResponse<>(e);
    }

}