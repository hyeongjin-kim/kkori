package com.kkori;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.kkori.dto.InterviewSession;
import com.kkori.dto.QuestionForm;
import com.kkori.dto.QuestionType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InterviewSession 비즈니스 로직 테스트")
public class InterviewSessionTest {

    private InterviewSession session;
    private List<QuestionForm> defaultQuestions;

    @BeforeEach
    void setUp() {
        defaultQuestions = new ArrayList<>();
        defaultQuestions.add(new QuestionForm(QuestionType.DEFAULT, 1, "자기소개를 해주세요"));
        defaultQuestions.add(new QuestionForm(QuestionType.DEFAULT, 2, "지원동기는 무엇인가요"));
        defaultQuestions.add(new QuestionForm(QuestionType.DEFAULT, 3, "장단점을 말해주세요"));

        session = new InterviewSession(new ArrayList<>(defaultQuestions));
    }

    @DisplayName("세션 초기화 테스트")
    @Test
    void should_set_first_default_question_as_current_and_parent_when_session_created() {
        // then
        assertThat(session.getCurrentQuestion().getQuestionType()).isEqualTo(QuestionType.DEFAULT);
        assertThat(session.getCurrentQuestion().getQuestionId()).isEqualTo(1);
        assertThat(session.getCurrentQuestion().getQuestionText()).isEqualTo("자기소개를 해주세요");
        assertThat(session.getParentQuestion()).isEqualTo(session.getCurrentQuestion());
    }

    @Nested
    @DisplayName("기본 질문 관련 테스트")
    class DefaultQuestionTests {

        @DisplayName("기본 질문 선택시 해당 질문ID로 부모질문과 현재질문이 모두 변경된다")
        @Test
        void should_update_both_parent_and_current_question_when_selecting_default_question() {
            // when
            session.getNextQuestions("답변 1");
            QuestionForm selectedQuestion = session.selectQuestion(QuestionType.DEFAULT, 2, "지원동기는 무엇인가요");
            session.getNextQuestions("답변 2");

            // then
            assertThat(session.getParentQuestion().getQuestionId()).isEqualTo(2);
            assertThat(session.getCurrentQuestion().getQuestionId()).isEqualTo(2);
            assertThat(selectedQuestion).isEqualTo(session.getCurrentQuestion());
        }

        @DisplayName("기본 질문 선택시 해당 질문이 기본질문 목록에서 제거된다")
        @Test
        void should_remove_selected_question_from_default_questions_list() {
            // given
            int originalSize = session.getDefaultQuestions().size();

            // when
            session.getNextQuestions("답변 1");
            session.selectQuestion(QuestionType.DEFAULT, 1, "자기소개를 해주세요");
            session.getNextQuestions("답변 2");

            // then
            assertThat(session.getDefaultQuestions()).hasSize(originalSize - 1);
            assertThat(session.getDefaultQuestions().get(0).getQuestionId()).isEqualTo(3);
        }

        @DisplayName("모든 기본질문이 소진되어도 시스템이 정상적으로 동작한다")
        @Test
        void should_handle_empty_default_questions_gracefully() {
            // given - 모든 기본 질문 소진
            session.getNextQuestions("답변 1");
            session.selectQuestion(QuestionType.DEFAULT, 2, "지원동기는 무엇인가요");
            session.getNextQuestions("답변 2");
            session.selectQuestion(QuestionType.DEFAULT, 3, "장단점을 말해주세요");
            session.getNextQuestions("답변 3");

            // when & then
            assertThatCode(() -> session.getNextQuestions("마지막 답변"))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("커스텀 질문 관련 테스트")
    class CustomQuestionTests {

        @DisplayName("커스텀 질문 생성시 해당 질문ID로 부모질문과 현재질문이 모두 변경된다")
        @Test
        void should_update_both_parent_and_current_question_when_creating_custom_question() {
            // when
            session.getNextQuestions("답변 1");
            int expected = session.getNewQuestionId();
            QuestionForm customQuestion = session.createCustomQuestion("커스텀 질문");
            session.getNextQuestions("답변 2");

            // then
            assertThat(session.getParentQuestion().getQuestionType()).isEqualTo(QuestionType.CUSTOM);
            assertThat(session.getParentQuestion().getQuestionId()).isEqualTo(expected);
            assertThat(session.getCurrentQuestion()).isEqualTo(customQuestion);
        }

        @DisplayName("커스텀 질문 생성시 질문ID가 자동으로 증가한다")
        @Test
        void should_auto_increment_question_id_when_creating_custom_questions() {
            // when
            session.getNextQuestions("답변 1");
            QuestionForm custom1 = session.createCustomQuestion("첫 번째 커스텀 질문");
            session.getNextQuestions("답변 2");
            QuestionForm custom2 = session.createCustomQuestion("두 번째 커스텀 질문");

            // then
            assertThat(custom1.getQuestionType()).isEqualTo(QuestionType.CUSTOM);
            assertThat(custom2.getQuestionType()).isEqualTo(QuestionType.CUSTOM);
            assertThat(custom1.getQuestionId()).isLessThan(custom2.getQuestionId());
            assertThat(session.getNewQuestionId()).isGreaterThan(custom2.getQuestionId());
        }

        @DisplayName("커스텀 질문 선택시 기본질문 목록은 변경되지 않는다")
        @Test
        void should_not_change_default_questions_when_creating_custom_question() {
            // given
            int originalSize = session.getDefaultQuestions().size();

            // when
            session.getNextQuestions("답변 1");
            session.createCustomQuestion("커스텀 질문");
            session.getNextQuestions("답변 2");

            // then
            assertThat(session.getDefaultQuestions()).hasSize(originalSize);
        }
    }

    @Nested
    @DisplayName("꼬리 질문 관련 테스트")
    class TailQuestionTests {

        @DisplayName("꼬리 질문 선택시 부모질문은 유지되고 현재질문만 변경된다")
        @Test
        void should_keep_parent_question_and_only_update_current_when_selecting_tail_question() {
            // given
            session.getNextQuestions("답변 1");
            session.selectQuestion(QuestionType.DEFAULT, 2, "지원동기는 무엇인가요");
            QuestionForm originalParent = session.getParentQuestion();
            session.getNextQuestions("답변 2");

            // when
            QuestionForm tailQuestion = session.selectQuestion(QuestionType.TAIL, 201, "구체적으로 어떤 경험이 있나요?");

            // then
            assertThat(session.getParentQuestion()).isEqualTo(originalParent);
            assertThat(session.getCurrentQuestion().getQuestionType()).isEqualTo(QuestionType.TAIL);
            assertThat(session.getCurrentQuestion().getQuestionId()).isEqualTo(201);
        }

        @DisplayName("꼬리 질문 생성시 부모질문의 타입과 ID가 올바르게 설정된다")
        @Test
        void should_set_parent_info_correctly_for_tail_questions() {
            // given
            QuestionForm parentQuestion = session.createCustomQuestion("커스텀 질문");
            QuestionForm nextQuestion = session.getNextQuestions("답변 1").getFirst();

            // when
            QuestionForm tailQuestion = session.selectQuestion(nextQuestion.getQuestionType(), nextQuestion.getQuestionId(), nextQuestion.getQuestionText());

            // then
            assertThat(tailQuestion.getParentQuestionType()).isEqualTo(QuestionType.CUSTOM);
            assertThat(tailQuestion.getParentQuestionId()).isEqualTo(parentQuestion.getQuestionId());
        }

        @DisplayName("꼬리 질문 선택시 기본질문 목록은 변경되지 않는다")
        @Test
        void should_not_change_default_questions_when_selecting_tail_question() {
            // given
            int originalSize = session.getDefaultQuestions().size();

            // when
            session.getNextQuestions("답변 1");
            session.selectQuestion(QuestionType.TAIL, 201, "꼬리 질문");
            session.getNextQuestions("답변 3");

            // then
            assertThat(session.getDefaultQuestions()).hasSize(originalSize);
        }
    }

    @Nested
    @DisplayName("답변 저장 관련 테스트")
    class AnswerSavingTests {

        @DisplayName("답변 저장 후 다음질문 목록을 제공한다")
        @Test
        void should_provide_next_questions_after_saving_answer() {
            // given & when
            List<QuestionForm> nextQuestions = session.getNextQuestions("저는 개발자입니다");

            // then
            assertThat(nextQuestions).hasSize(3); // 꼬리질문 2개 + 다음 기본질문 1개

            // 꼬리질문들 확인
            List<QuestionForm> tailQuestions = nextQuestions.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.TAIL)
                    .toList();
            assertThat(tailQuestions).hasSize(2);
            assertThat(tailQuestions.get(0).getQuestionText()).contains("꼬리질문 1번");
            assertThat(tailQuestions.get(1).getQuestionText()).contains("꼬리질문 2번");

            // 다음 기본질문 확인
            List<QuestionForm> defaultQuestion = nextQuestions.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.DEFAULT)
                    .toList();
            assertThat(defaultQuestion).hasSize(1);
        }

        @DisplayName("현재 질문에 대한 답변이 올바르게 저장된다")
        @Test
        void should_save_answer_for_current_question_correctly() {
            // given
            QuestionForm question = session.getCurrentQuestion();
            String answer = "저는 5년차 개발자입니다";

            // when
            session.getNextQuestions(answer);

            // then
            assertThat(session.getQuestionAnswer()).containsKey(question);
            assertThat(session.getQuestionAnswer().get(question)).isEqualTo(answer);
        }

        @DisplayName("여러 질문에 대한 답변들이 모두 저장된다")
        @Test
        void should_save_answers_for_multiple_questions() {
            // given & when
            session.getNextQuestions("저는 개발자입니다");
            QuestionForm q1 = session.getCurrentQuestion();

            QuestionForm q2 = session.createCustomQuestion("커스텀 질문");
            session.getNextQuestions("커스텀 답변입니다");

            QuestionForm q3 = session.selectQuestion(QuestionType.TAIL, 201, "꼬리 질문");
            session.getNextQuestions("꼬리 답변입니다");

            // then
            assertThat(session.getQuestionAnswer()).hasSize(3);
            assertThat(session.getQuestionAnswer().get(q1)).isEqualTo("저는 개발자입니다");
            assertThat(session.getQuestionAnswer().get(q2)).isEqualTo("커스텀 답변입니다");
            assertThat(session.getQuestionAnswer().get(q3)).isEqualTo("꼬리 답변입니다");
        }
    }

    @Nested
    @DisplayName("전체 인터뷰 시나리오 테스트")
    class FullInterviewScenarioTests {

        @DisplayName("기본질문부터 꼬리질문과 커스텀질문을 거쳐 완전한 인터뷰 플로우가 동작한다")
        @Test
        void should_complete_full_interview_flow_from_default_to_tail_and_custom_questions() {
            // 1. 첫 번째 기본질문에 답변
            QuestionForm firstQuestion = session.getCurrentQuestion();
            List<QuestionForm> nextOptions1 = session.getNextQuestions("저는 5년차 백엔드 개발자입니다");

            // 2. 꼬리질문 선택 및 답변
            QuestionForm tailQuestion = nextOptions1.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.TAIL)
                    .findFirst()
                    .orElseThrow();
            session.selectQuestion(tailQuestion.getQuestionType(), tailQuestion.getQuestionId(),
                    tailQuestion.getQuestionText());
            List<QuestionForm> nextOptions2 = session.getNextQuestions("주로 Spring Boot와 JPA를 사용합니다");

            // 3. 커스텀질문 생성 및 답변
            QuestionForm customQuestion = session.createCustomQuestion("가장 어려웠던 프로젝트는?");
            List<QuestionForm> nextOptions3 = session.getNextQuestions("대용량 트래픽 처리 프로젝트였습니다");

            // 4. 다시 꼬리질문 선택
            QuestionForm tailQuestion2 = nextOptions3.stream()
                    .filter(q -> q.getQuestionType() == QuestionType.TAIL)
                    .findFirst()
                    .orElseThrow();
            session.selectQuestion(tailQuestion2.getQuestionType(), tailQuestion2.getQuestionId(),
                    tailQuestion2.getQuestionText());
            session.getNextQuestions("Redis와 ElasticSearch를 활용했습니다");

            // then - 최종 상태 검증
            assertThat(session.getQuestionAnswer()).hasSize(4);
            assertThat(session.getCurrentQuestion().getQuestionType()).isEqualTo(QuestionType.TAIL);
            assertThat(session.getParentQuestion()).isEqualTo(customQuestion); // 마지막 non-tail 질문
        }
    }
}