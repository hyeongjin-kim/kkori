package com.kkori.component.interview;

public enum InterviewStatus {
    BEFORE_INTERVIEW("beforeInterview"),
    QUESTION_PRESENTED("questionPresented"),
    NEXT_QUESTION_PRESENTED("nextQuestionPresented"),
    CUSTOM_QUESTION_SELECTED("customQuestionSelected");

    private final String statusName;

    InterviewStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public static InterviewStatus fromStatusName(String statusName) {
        for (InterviewStatus status : values()) {
            if (status.statusName.equals(statusName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + statusName);
    }

    public static boolean isTransitionalStatus(String statusName) {
        return "answerStart".equals(statusName) || 
               "answerSubmit".equals(statusName) || 
               "nextQuestionSelected".equals(statusName) ||
               "customQuestionStart".equals(statusName) ||
               "customQuestionCreated".equals(statusName);
    }
}