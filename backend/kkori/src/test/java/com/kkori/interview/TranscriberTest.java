package com.kkori.interview;

import static org.assertj.core.api.Assertions.assertThat;

import com.kkori.component.Transcriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Transcriber 로직 테스트")
@Tag("integration") // Jenkins에서 제외할 태그
public class TranscriberTest {

    @Autowired
    private Transcriber transcriber;

    /**
     * 임시로 로컬에 있는 m4a를 사용합니다.
     */
    @Test
    @DisplayName("생성된 오디오 파일을 API를 통해 텍스트로 변환해야 한다")
    void should_transcribe_audio_file_through_API() {
        //given
        String filePath = getClass().getClassLoader().getResource("demo.m4a").getPath();
        //when
        String transcript = transcriber.transcribe(filePath);
        //then
        assertThat(transcript).isNotEmpty();
    }
}
