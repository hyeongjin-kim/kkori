package com.kkori.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfileResponse {

    private Properties properties;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        private String nickname;
    }

}
