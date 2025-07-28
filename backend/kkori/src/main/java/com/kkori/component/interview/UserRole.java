package com.kkori.component.interview;

import lombok.Getter;

@Getter
public enum UserRole {
    INTERVIEWER("면접관"),
    INTERVIEWEE("면접자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}
