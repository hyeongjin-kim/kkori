package com.kkori.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

}
