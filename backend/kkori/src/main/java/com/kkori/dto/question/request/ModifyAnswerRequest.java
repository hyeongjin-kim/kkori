package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyAnswerRequest {

    @NotNull(message = "매핑 ID는 필수입니다.")
    private Long mapId;

    @NotBlank(message = "새 답변 내용은 필수입니다.")
    private String newExpectedAnswer;

    @Builder
    public ModifyAnswerRequest(Long mapId, String newExpectedAnswer) {
        this.mapId = mapId;
        this.newExpectedAnswer = newExpectedAnswer;
    }
}