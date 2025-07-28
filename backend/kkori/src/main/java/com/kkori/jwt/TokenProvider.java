package com.kkori.jwt;

import com.kkori.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.time.Duration;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${jwt.ACCESS_TOKEN_MINUTE_TIME}")
    private int accessTokenExpireMinutes;

    @Getter
    @Value("${jwt.REFRESH_TOKEN_MINUTE_TIME}")
    private int refreshTokenExpireMinutes;

    public Token generateAccessToken(User user) {
        return generateToken(user, accessTokenExpireMinutes);
    }

    public Token generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpireMinutes);
    }

    public Token generateToken(User user, int minutes) {
        Duration expiredAt = Duration.ofMinutes(minutes);
        Date now = new Date();
        String token = makeToken(user, new Date(now.getTime() + expiredAt.toMillis()));
        return new Token(token);
    }

    private String makeToken(User user, Date expiry) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("userId", user.getUserId())
                .claim("nickName", user.getNickname())
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT signature", e);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired JWT token", e);
        } catch (MalformedJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed JWT token", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
        return claims.get("userId", Long.class);
    }

    public String getToken(HttpHeaders headers) {
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        return token;
    }

}