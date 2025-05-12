package com.modive.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modive.authservice.exception.CustomException;
import com.modive.authservice.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
public class ApiResponse<T> {
    private final int code;
    private final String message;

    @JsonInclude(NON_EMPTY)
    private T data;

    // 응답 코드만 반환
    public ApiResponse(HttpStatus httpStatus) {
        Status status = new Status(httpStatus);
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    // 응답 코드와 단일 데이터 반환
    public ApiResponse(HttpStatus httpStatus, T result) {
        Status status = new Status(httpStatus);
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = result;
    }

    // 에러 처리
    public ApiResponse(CustomException e) {
        Status status = new Status(e.getErrorCode());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    @Getter
    private class Status {
        private final int code;
        private final String message;

        // 커스텀 에러 코드 처리
        public Status(ErrorCode errorCode) {
            this.code = errorCode.getHttpStatus().value();
            this.message = errorCode.getMessage();
        }

        // 일반 응답 반환(OK, CREATED, ACCEPTED 등)
        public Status(HttpStatus httpStatus) {
            this.code = httpStatus.value();
            this.message = httpStatus.getReasonPhrase();
        }
    }
}