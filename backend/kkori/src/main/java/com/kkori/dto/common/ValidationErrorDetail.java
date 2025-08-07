package com.kkori.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorDetail {
    
    private String field;
    private String message;
}