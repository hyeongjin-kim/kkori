package com.kkori.dto.interview.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequest {
    private String mode; // "SOLO_PRACTICE" | "PAIR_INTERVIEW"
    private Long questionSetId;
}