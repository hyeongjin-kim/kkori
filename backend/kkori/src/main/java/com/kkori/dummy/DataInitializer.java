package com.kkori.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final DummyDataService dummyDataService;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 개발/로컬 환경에서만 더미 데이터 생성
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevOrLocal = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("dev") || profile.equals("local"));

        // 프로파일이 없는 경우도 개발환경으로 간주
        if (activeProfiles.length == 0 || isDevOrLocal) {
            try {
                log.info("=== 서버 시작 시 더미 데이터 생성 시작 ===");
                dummyDataService.createDummyData();
                log.info("=== 더미 데이터 생성 완료 ===");
            } catch (Exception e) {
                log.error("더미 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
                // 더미 데이터 생성 실패가 서버 시작을 막지 않도록 예외를 다시 던지지 않음
            }
        } else {
            log.info("운영 환경에서는 더미 데이터를 생성하지 않습니다. Active profiles: {}",
                    Arrays.toString(activeProfiles));
        }
    }
}
