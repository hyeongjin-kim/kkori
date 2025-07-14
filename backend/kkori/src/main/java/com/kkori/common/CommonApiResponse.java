package com.kkori.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    public static <T> CommonApiResponse<T> ok(T data) {
        return new CommonApiResponse<>(true, data, null);
    }

    public static <T> CommonApiResponse<T> fail(String message) {
        return new CommonApiResponse<>(false, null, message);
    }
    
}
