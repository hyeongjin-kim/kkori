package com.kkori.config;

import com.kkori.jwt.JwtAuthenticationFilter;
import com.kkori.jwt.TokenProvider;
import com.kkori.service.KakaoOAuth2Service;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Profile("!test")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final KakaoOAuth2Service kakaoOAuth2Service;

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsString;

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    );

    private static final List<String> ALLOWED_HEADERS = List.of(
            "Authorization", "Content-Type"
    );

    private static final String[] PERMIT_ALL_PATHS = {
            "/",
            "/oauth2/authorization/kakao",
            "/oauth2/authorization/kakao/callback",
            "/ws/**",
            "/actuator/**",
            "/api/login/guest",
            "/api/questionsets",
            "/api/questionsets/*",
            "/api/admin/*"
    };

    private static final long HSTS_MAX_AGE_IN_SECONDS = 31536000L;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .anyRequest().authenticated()
                )

//                // 모든 요청 HTTPS 강제 적용
//                .requiresChannel(channel -> channel.anyRequest().requiresSecure())

                // 보안 헤더 설정
                .headers(headers -> headers
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'"))
                        // HSTS 정책 적용
                        .httpStrictTransportSecurity(hsts ->
                                hsts.includeSubDomains(true).maxAgeInSeconds(HSTS_MAX_AGE_IN_SECONDS)) // HSTS 1년
                        .frameOptions(FrameOptionsConfig::sameOrigin)
                        .referrerPolicy(referrer ->
                                referrer.policy(
                                        ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN))
                )

                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, kakaoOAuth2Service),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.asList(allowedOriginsString.split(","));
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.addAllowedOrigin("https://kauth.kakao.com");
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
