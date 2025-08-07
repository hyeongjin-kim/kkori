package com.kkori.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    private String type;
    private String roomId;
    private Long senderId;
    private Long receiverId;
    private String sdp;

    public void setTypeReceivedOffer() {
        this.type = "received-offer";
    }
    
    public void setTypeReceivedAnswer() {
        this.type = "received-answer";
    }
}