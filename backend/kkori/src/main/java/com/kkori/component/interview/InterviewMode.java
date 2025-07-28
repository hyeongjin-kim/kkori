package com.kkori.component.interview;

import lombok.Getter;

@Getter
public enum InterviewMode {
    SOLO_PRACTICE("혼자 연습하기"),
    PAIR_INTERVIEW("함께 연습하기");

    private final String description;

    InterviewMode(String description) {
        this.description = description;
    }
}
