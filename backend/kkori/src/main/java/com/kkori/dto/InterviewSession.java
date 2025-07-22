package com.kkori.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterviewSession {
    private String currentId;
    private String parentId;
    private String tqIdx = "TQ1";
    private String cqIdx = "CQ1";

    private List<TailQuestion> tailQuestions = new ArrayList<>();
    private List<CustomQuestion> customQuestions = new ArrayList<>();
    private Map<String, String> answers = new HashMap<>();

    private Map<String, TailQuestion> pendingTailQuestions = new TreeMap<>();

    public void selectQuestion(String questionId) {
        if (!questionId.startsWith("TQ")) {
            this.parentId = questionId;
        } else {
            tailQuestions.add(pendingTailQuestions.get(questionId));
            pendingTailQuestions.clear();
        }
        this.currentId = questionId;
    }

    public CustomQuestion createCustomQuestion(String customQuestionText) {
        CustomQuestion customQuestion = new CustomQuestion(cqIdx, customQuestionText);
        customQuestions.add(customQuestion);
        cqIdx = "CQ" + (Integer.parseInt(cqIdx.substring(2)) + 1);
        return customQuestion;
    }

    public List<TailQuestion> saveAnswer(String answerText) {
        List<TailQuestion> tailQuestionList = new ArrayList<>();

        if (currentId != null) {
            answers.put(currentId, answerText);
            generateTailQuestions(answerText);
            for (String key : pendingTailQuestions.keySet()) {
                tailQuestionList.add(pendingTailQuestions.get(key));
            }
        }
        return tailQuestionList;
    }

    private void generateTailQuestions(String answerText) {
        //pendingTailQuestions.clear();
        pendingTailQuestions.put(tqIdx, new TailQuestion(tqIdx, "꼬리질문 1번 입니다", parentId));
        tqIdx = "TQ" + (Integer.parseInt(tqIdx.substring(2)) + 1);
        pendingTailQuestions.put(tqIdx, new TailQuestion(tqIdx, "꼬리질문 2번 입니다", parentId));
        tqIdx = "TQ" + (Integer.parseInt(tqIdx.substring(2)) + 1);
    }
}
