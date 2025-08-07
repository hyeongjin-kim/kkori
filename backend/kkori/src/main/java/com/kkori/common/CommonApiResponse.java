package com.kkori.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommonApiResponse<T> {

    private boolean success;
    private int status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private Instant timestamp;

    public static <T> CommonApiResponse<T> ok(T data) {
        return CommonApiResponse.<T>builder()
                .success(true)
                .status(200)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> CommonApiResponse<T> ok(T data, String message) {
        return CommonApiResponse.<T>builder()
                .success(true)
                .status(200)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> CommonApiResponse<T> fail(int status, String message) {
        return CommonApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> CommonApiResponse<T> fail(int status, String message, T data) {
        return CommonApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

}
