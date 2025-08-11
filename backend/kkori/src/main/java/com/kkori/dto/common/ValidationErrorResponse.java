package com.kkori.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
    
    private List<ValidationErrorDetail> errors;
}