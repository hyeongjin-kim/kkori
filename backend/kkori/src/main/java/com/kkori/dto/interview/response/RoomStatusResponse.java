package com.kkori.dto.interview.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusResponse {
    private String status; // "WAITING", "IN_PROGRESS", "COMPLETED"
    private int userCount;
    private int maxUsers;
}