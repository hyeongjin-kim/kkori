package com.kkori.dto.question.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class CreateNewQuestionSetRequest {

    @NotBlank
    private String title;

    private String description;

    private List<Long> tagIds;

    @NotEmpty
    private List<CreateQuestionRequest> questions;

    private Long parentVersionId;

}
