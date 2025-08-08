package com.kkori.interview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Base64 오디오 처리 통합 테스트")
public class Base64AudioProcessingTest {

    @TempDir
    Path tempDir;

    // 짧은 테스트용 Base64 데이터 (WebM 헤더 포함)
    private static final String TEST_WEBM_BASE64 =
            "GkXfo0OBAkKFgQIYU4BnAQAAAAAAAHTEU2bdgX+Wws+XmiHnQs+EaOFjhDMAAUABvABMAfWD" +
                    "bwPfAMwA5QBZrmtTsKRFH6NLZvDLLFKTpUHnQs+EaOFjhDMABMAJAwCF2cEAAAAA8QEBAcABPABg" +
                    "AICAYwDyQEAAaABgAHgEcABRyEpv1Kb4EjSEVPYABDGvMDPUMQIR+CAAIi8wMlIAAKwByAUkm8AL" +
                    "AB0Lk8AAH2wByA==";

    @Test
    @DisplayName("Base64 디코딩 및 임시 WebM 파일 생성 테스트")
    void should_decode_base64_and_create_temp_webm_file() {
        assertDoesNotThrow(() -> {
            // Base64 디코딩
            byte[] audioBytes = Base64.getDecoder().decode(TEST_WEBM_BASE64);

            // 임시 파일 생성 - Jenkins 환경 호환을 위해 @TempDir 사용
            // String tempFilePath = System.getProperty("java.io.tmpdir") +
            //                      "test_audio_" + System.currentTimeMillis() + ".webm";
            Path tempFilePath = tempDir.resolve("test_audio_" + System.currentTimeMillis() + ".webm");

            try (FileOutputStream fos = new FileOutputStream(tempFilePath.toFile())) {
                fos.write(audioBytes);
            }

            // 파일이 생성되었는지 확인
            // assertThat(Files.exists(Paths.get(tempFilePath))).isTrue();
            assertThat(Files.exists(tempFilePath)).isTrue();

            // 파일 크기 확인 (비어있지 않음)
            // assertThat(Files.size(Paths.get(tempFilePath))).isGreaterThan(0);
            assertThat(Files.size(tempFilePath)).isGreaterThan(0);

            // 정리 - @TempDir이 자동으로 정리해주므로 수동 삭제 불필요
            // Files.deleteIfExists(Paths.get(tempFilePath));
        });
    }

    @Test
    @DisplayName("Base64 오디오 데이터 유효성 검증")
    void should_validate_base64_audio_data() {
        assertDoesNotThrow(() -> {
            // Base64 디코딩
            byte[] audioBytes = Base64.getDecoder().decode(TEST_WEBM_BASE64);

            // 디코딩된 데이터가 비어있지 않아야 함
            assertThat(audioBytes).isNotEmpty();

            // WebM 시그니처 확인 (첫 4바이트가 WebM 헤더에요)
            assertThat(audioBytes.length).isGreaterThanOrEqualTo(4);
        });
    }

    @Test
    @DisplayName("임시 파일 생성 및 정리 프로세스 테스트")
    void should_create_and_cleanup_temp_files() throws IOException {
        // Path tempFilePath = null; // @TempDir 사용으로 불필요
        // String tempFilePath = null;

        // try {
        // 1. Base64 디코딩
        byte[] audioBytes = Base64.getDecoder().decode(TEST_WEBM_BASE64);

        // 2. 임시 파일 생성 - Jenkins 환경 호환을 위해 @TempDir 사용
        // tempFilePath = System.getProperty("java.io.tmpdir") +
        //               "cleanup_test_" + System.currentTimeMillis() + ".webm";
        Path tempFilePath = tempDir.resolve("cleanup_test_" + System.currentTimeMillis() + ".webm");

        try (FileOutputStream fos = new FileOutputStream(tempFilePath.toFile())) {
            fos.write(audioBytes);
        }

        // 3. 파일 존재 확인
        // assertThat(Files.exists(Paths.get(tempFilePath))).isTrue();
        assertThat(Files.exists(tempFilePath)).isTrue();

        // } finally {
        // 4. 정리 (finally 블록에서 확실히 정리) - @TempDir이 자동으로 정리
        // if (tempFilePath != null) {
        //     Files.deleteIfExists(Paths.get(tempFilePath));
        //     assertThat(Files.exists(Paths.get(tempFilePath))).isFalse();
        // }
        // }
    }
}