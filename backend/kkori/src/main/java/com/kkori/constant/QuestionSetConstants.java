package com.kkori.constant;

public final class QuestionSetConstants {

    private QuestionSetConstants() {
    }

    public static final class ResponseMessage {
        public static final String QUESTION_SET_CREATED = "질문 세트와 첫 질문이 생성되었습니다";
        public static final String NEW_VERSION_CREATED = "기존 질문 세트에서 새 버전이 생성되었습니다";
        public static final String QUESTION_ADDED = "질문이 추가되었습니다";

        private ResponseMessage() {
        }
    }

    public static final class DefaultValue {
        public static final Integer INITIAL_VERSION_NUMBER = 1;
        public static final Integer NEXT_VERSION_NUMBER = 2;
        public static final Boolean DEFAULT_PUBLIC_STATUS = false;
        public static final Integer INITIAL_DISPLAY_ORDER = 1;

        private DefaultValue() {
        }
    }

}