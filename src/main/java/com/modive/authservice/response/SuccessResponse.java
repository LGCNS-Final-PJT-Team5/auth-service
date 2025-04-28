package com.modive.authservice.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {
    private int status;
    private String message;
    private T data;

    private SuccessResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(HttpStatus status, String message, T data) {
        return new SuccessResponse<>(status, message, data);
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(HttpStatus.OK, message, data);
    }

    public static <T> SuccessResponse<T> of(HttpStatus status, T data) {
        return new SuccessResponse<>(status, status.getReasonPhrase(), data);
    }

    public static <T> SuccessResponse<T> ok(T data) {
        return new SuccessResponse<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), data);
    }

    public static <T> SuccessResponse<T> created(T data) {
        return new SuccessResponse<>(HttpStatus.CREATED, HttpStatus.CREATED.getReasonPhrase(), data);
    }
}