package com.kkori.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class STOMPErrorResponse {
    private String requestType;
    private String message;
    private long timestamp;
}
