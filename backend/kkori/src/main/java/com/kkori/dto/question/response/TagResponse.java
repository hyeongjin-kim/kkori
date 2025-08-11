package com.kkori.dto.question.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TagResponse {

    private Long id;
    private String tag;

    @Builder
    public TagResponse(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }

}
