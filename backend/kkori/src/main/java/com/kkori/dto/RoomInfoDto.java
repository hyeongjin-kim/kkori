package com.kkori.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoDto {
    private String questionSetId;
    private String status;
    private String currentQuestion; // QuestionDto
    private String userCount;
    private String users; // List<userInfoDto>
    private String createdAt;
    private String interviewId;
}