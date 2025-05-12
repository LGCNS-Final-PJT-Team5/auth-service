package com.modive.authservice.exception;

import org.springframework.http.HttpStatus;

// 도메인별 에러 코드 구체화
public interface ErrorCode {

    HttpStatus getHttpStatus();
    String getMessage();

}