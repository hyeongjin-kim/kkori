package com.kkori.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // 기본 생성자 추가
public class STOMPMessage {
    private String type;
    private String sessionId;
    private String userId;
    private String payload;
    private String timestamp;
}