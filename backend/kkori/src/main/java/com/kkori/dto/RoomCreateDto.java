package com.kkori.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomCreateDto {
    private String questionSetId;
    private String userName;
}