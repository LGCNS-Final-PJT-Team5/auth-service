package com.modive.authservice.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponseNoData<T> {
    private int status;
    private String message;

    private SuccessResponseNoData(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public static <T> SuccessResponseNoData<T> of(String message) {
        return new SuccessResponseNoData<>(HttpStatus.OK, message);
    }
}