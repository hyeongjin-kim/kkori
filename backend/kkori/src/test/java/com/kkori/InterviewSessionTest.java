package com.kkori;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.kkori.dto.CustomQuestion;
import com.kkori.dto.InterviewSession;
import com.kkori.dto.TailQuestion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InterviewSession 비즈니스 로직 테스트")
public class InterviewSessionTest {

    private InterviewSession session;

    @BeforeEach
    void setUp() {
        session = new InterviewSession();
    }

    @DisplayName("기본 질문 선택시 PID와 currentID가 모두 갱신되어야 한다")
    @Test
    void should_update_PID_and_currentID_when_select_basic_question() {
        // when
        session.selectQuestion("1");

        // then
        assertThat(session.getParentId()).isEqualTo("1");
        assertThat(session.getCurrentId()).isEqualTo("1");
    }

    @DisplayName("커스텀 질문 선택시 PID와 currentID가 모두 갱신되어야 한다")
    @Test
    void should_update_PID_and_currentID_when_select_custom_question() {
        // given
        session.createCustomQuestion("사용자 정의 질문");

        // when
        session.selectQuestion("CQ1");

        // then
        assertThat(session.getParentId()).isEqualTo("CQ1");
        assertThat(session.getCurrentId()).isEqualTo("CQ1");
    }

    @DisplayName("꼬리 질문 선택시 PID는 유지되고 currentID만 갱신되어야 한다")
    @Test
    void should_keep_PID_and_only_update_currentID_when_select_tail_question() {
        // given
        session.selectQuestion("1");  // PID = "1"
        session.saveAnswer("기본 답변");
        List<TailQuestion> tailQuestions = session.saveAnswer("기본 답변");

        // when
        session.selectQuestion("TQ1");

        // then
        assertThat(session.getParentId()).isEqualTo("1");  // PID 유지
        assertThat(session.getCurrentId()).isEqualTo("TQ1");
    }

    @DisplayName("답변을 저장하면 현재 질문 ID와 함께 저장되어야 한다")
    @Test
    void should_save_answer_with_current_question_id() {
        // given
        session.selectQuestion("1");

        // when
        session.saveAnswer("제 답변입니다");

        // then
        assertThat(session.getAnswers()).containsEntry("1", "제 답변입니다");
    }

    @DisplayName("현재 질문이 없을 때 답변 저장을 시도하면 저장되지 않아야 한다")
    @Test
    void should_not_save_answer_when_no_current_question() {
        // when
        session.saveAnswer("답변");

        // then
        assertThat(session.getAnswers()).isEmpty();
    }

    @DisplayName("꼬리질문 생성시 2개의 질문이 임시 저장되어야 한다")
    @Test
    void should_generate_two_pending_tail_questions() {
        // given
        session.selectQuestion("1");  // PID 설정

        // when
        List<TailQuestion> tailQuestions = session.saveAnswer("기본 답변");

        // then
        assertThat(tailQuestions).hasSize(2);
        assertThat(session.getPendingTailQuestions()).hasSize(2);
        assertThat(session.getPendingTailQuestions()).containsKeys("TQ1", "TQ2");

        // 실제 리스트에는 아직 추가되지 않음
        assertThat(session.getTailQuestions()).isEmpty();
    }

    @DisplayName("꼬리질문 생성시 부모 ID가 현재 PID로 설정되어야 한다")
    @Test
    void should_set_parent_id_to_current_PID_when_generate_tail_questions() {
        // given
        session.selectQuestion("1");  // PID = "1"

        // when
        List<TailQuestion> tailQuestions = session.saveAnswer("기본 답변");

        // then
        assertThat(tailQuestions.get(0).getParentId()).isEqualTo("1");
        assertThat(tailQuestions.get(1).getParentId()).isEqualTo("1");
    }

    @DisplayName("꼬리질문 선택시 선택된 질문은 실제 리스트로 이동하고 나머지는 삭제되어야 한다")
    @Test
    void should_move_selected_tail_question_and_clear_others() {
        // given
        session.selectQuestion("1");
        session.saveAnswer("기본 답변");

        // when
        session.selectQuestion("TQ1");

        // then
        assertThat(session.getTailQuestions()).hasSize(1);
        assertThat(session.getTailQuestions().get(0).getQuestionId()).isEqualTo("TQ1");
        assertThat(session.getPendingTailQuestions()).isEmpty();
    }

    @DisplayName("존재하지 않는 꼬리질문 ID를 선택해도 에러가 발생하지 않아야 한다")
    @Test
    void should_not_throw_error_when_select_non_existing_tail_question() {
        // when & then
        assertThatCode(() -> session.selectQuestion("TQ99"))
                .doesNotThrowAnyException();

        assertThat(session.getCurrentId()).isEqualTo("TQ99");
    }

    @DisplayName("커스텀 질문 생성시 ID가 순차적으로 증가해야 한다")
    @Test
    void should_increment_custom_question_id_sequentially() {
        // when
        CustomQuestion cq1 = session.createCustomQuestion("첫 번째 커스텀 질문");
        CustomQuestion cq2 = session.createCustomQuestion("두 번째 커스텀 질문");

        // then
        assertThat(cq1.getQuestionId()).isEqualTo("CQ1");
        assertThat(cq2.getQuestionId()).isEqualTo("CQ2");
        assertThat(session.getCqIdx()).isEqualTo("CQ3");  // 다음은 CQ3
    }

    @DisplayName("꼬리질문 생성시 ID가 순차적으로 증가해야 한다")
    @Test
    void should_increment_tail_question_id_sequentially() {
        // given
        session.selectQuestion("1");

        // when
        session.saveAnswer("첫 번째 답변");  // TQ1, TQ2 생성
        session.saveAnswer("두 번째 답변");  // TQ3, TQ4 생성

        // then
        assertThat(session.getTqIdx()).isEqualTo("TQ5");  // 다음은 TQ5
    }

    @DisplayName("여러 질문에 대한 답변들이 모두 저장되어야 한다")
    @Test
    void should_save_multiple_answers_for_different_questions() {
        // given & when
        session.selectQuestion("1");
        session.saveAnswer("기본질문 답변");

        session.selectQuestion("CQ1");
        session.saveAnswer("커스텀질문 답변");

        session.selectQuestion("TQ1");
        session.saveAnswer("꼬리질문 답변");

        // then
        assertThat(session.getAnswers()).hasSize(3);
        assertThat(session.getAnswers().get("1")).isEqualTo("기본질문 답변");
        assertThat(session.getAnswers().get("CQ1")).isEqualTo("커스텀질문 답변");
        assertThat(session.getAnswers().get("TQ1")).isEqualTo("꼬리질문 답변");
    }

    @DisplayName("전체 인터뷰 시나리오 테스트")
    @Test
    void should_complete_full_interview_scenario() {
        // 1. 기본질문 선택 및 답변
        session.selectQuestion("1");
        session.saveAnswer("저는 개발자입니다");

        // 2. 꼬리질문 생성
        List<TailQuestion> tailQuestions = session.saveAnswer("저는 개발자입니다");

        // 3. 꼬리질문 하나 선택 및 답변
        session.selectQuestion("TQ1");
        session.saveAnswer("5년차 백엔드 개발자입니다");

        // 4. 커스텀질문 생성 및 선택
        session.createCustomQuestion("가장 어려웠던 프로젝트는?");
        session.selectQuestion("CQ1");
        session.saveAnswer("대용량 트래픽 처리 프로젝트였습니다");

        // 5. 커스텀질문에서 꼬리질문 생성
        session.saveAnswer("대용량 트래픽 처리 프로젝트였습니다");
        session.selectQuestion("TQ3");
        session.saveAnswer("Redis와 ElasticSearch를 활용했습니다");

        // then - 최종 상태 검증
        assertThat(session.getAnswers()).hasSize(4);
        assertThat(session.getTailQuestions()).hasSize(2);  // TQ1, TQ3 선택됨
        assertThat(session.getCustomQuestions()).hasSize(1);
        assertThat(session.getCurrentId()).isEqualTo("TQ3");
        assertThat(session.getParentId()).isEqualTo("CQ1");  // 마지막 기본/커스텀 질문
    }

    @DisplayName("꼬리질문 생성시 질문 내용이 답변을 기반으로 생성되어야 한다")
    @Test
    void should_generate_question_text_based_on_answer() {
        // given
        session.selectQuestion("1");
        String answer = "저는 Java 개발자입니다";

        // when
        List<TailQuestion> tailQuestions = session.saveAnswer(answer);

        // then
        assertThat(tailQuestions.get(0).getQuestionText())
                .contains("꼬리질문 1번 입니다");
        assertThat(tailQuestions.get(1).getQuestionText())
                .contains("꼬리질문 2번 입니다");
    }
}