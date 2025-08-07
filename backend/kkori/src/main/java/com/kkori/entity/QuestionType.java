package com.kkori.entity;

import lombok.Getter;

@Getter
public enum QuestionType {
    CUSTOM(1),
    DEFAULT(2),
    TAIL(3);

    private final int code;

    QuestionType(int code) {
        this.code = code;
    }

    public static QuestionType fromCode(int code) {
        for (QuestionType t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        throw new IllegalArgumentException("알 수 없는 QuestionType code: " + code);
    }

}
