package com.kkori.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IceCandidate {
    private String roomId;
    private String candidate;
    private Long userId;
}
