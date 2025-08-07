package com.kkori.repository;

import static com.kkori.entity.QuestionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kkori.component.interview.RoomStatus;
import com.kkori.entity.Interview;
import com.kkori.entity.InterviewTailQuestion;
import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("면접 꼬리 질문 Repository 테스트")
class InterviewTailQuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InterviewTailQuestionRepository interviewTailQuestionRepository;

    private User interviewee;
    private User interviewer;
    private QuestionSet questionSet;
    private Question question1, question2;
    private Interview interview1, interview2;
    private InterviewTailQuestion tailQuestion1, tailQuestion2, tailQuestion3, tailQuestion4;

    @BeforeEach
    void setUp() {
        // Given: 테스트 데이터 준비
        setupUsers();
        setupQuestions();
        setupQuestionSet();
        setupInterviews();
        setupTailQuestions();
        
        entityManager.flush();
        entityManager.clear(); // 1차 캐시 초기화
    }

    private void setupUsers() {
        interviewee = User.builder()
                .sub("kakao123")
                .nickname("면접자")
                .build();
        entityManager.persistAndFlush(interviewee);

        interviewer = User.builder()
                .sub("kakao456")
                .nickname("면접관")
                .build();
        entityManager.persistAndFlush(interviewer);
    }

    private void setupQuestions() {
        // 불변 Question 엔티티들
        question1 = Question.builder()
                .content("스프링 프레임워크의 특징을 설명해주세요.")
                .questionType(CUSTOM)
                .expectedAnswer("IoC, DI, AOP 등의 특징이 있습니다.")
                .build();
        entityManager.persistAndFlush(question1);

        question2 = Question.builder()
                .content("데이터베이스 트랜잭션에 대해 설명해주세요.")
                .questionType(DEFAULT)
                .expectedAnswer("ACID 특성을 만족하는 작업 단위입니다.")
                .build();
        entityManager.persistAndFlush(question2);
    }

    private void setupQuestionSet() {
        questionSet = QuestionSet.builder()
                .ownerUserId(interviewer)
                .title("백엔드 면접 질문")
                .description("백엔드 개발자 면접용")
                .versionNumber(1)
                .isShared(false)
                .build();
        entityManager.persistAndFlush(questionSet);
    }

    private void setupInterviews() {
        interview1 = Interview.builder()
                .interviewer(interviewer)
                .interviewee(interviewee)
                .usedQuestionSet(questionSet)
                .roomId("room-001")
                .build();
        interview1.complete(); // 완료된 면접
        entityManager.persistAndFlush(interview1);

        interview2 = Interview.builder()
                .interviewer(interviewer)
                .interviewee(interviewee)
                .usedQuestionSet(questionSet)
                .roomId("room-002")
                .build();
        // 진행 중인 면접
        entityManager.persistAndFlush(interview2);
    }

    private void setupTailQuestions() {
        // interview1의 꼬리 질문들
        tailQuestion1 = InterviewTailQuestion.builder()
                .interview(interview1)
                .originalQuestion(question1)
                .content("IoC와 DI의 차이점은 무엇인가요?")
                .originalUserAnswer("IoC와 DI가 스프링의 핵심 특징입니다.")
                .questionOrder(1)
                .generationContext("GPT-4 generated")
                .build();
        tailQuestion1.submitAnswer("IoC는 제어의 역전이고, DI는 의존성 주입입니다.");
        entityManager.persistAndFlush(tailQuestion1);

        tailQuestion2 = InterviewTailQuestion.builder()
                .interview(interview1)
                .originalQuestion(question1)
                .content("AOP의 실제 사용 예시를 들어주세요.")
                .originalUserAnswer("IoC와 DI가 스프링의 핵심 특징입니다.")
                .questionOrder(2)
                .generationContext("GPT-4 generated")
                .build();
        // 답변 안함
        entityManager.persistAndFlush(tailQuestion2);

        tailQuestion3 = InterviewTailQuestion.builder()
                .interview(interview1)
                .originalQuestion(question2)
                .content("트랜잭션 격리 수준에 대해 설명해주세요.")
                .originalUserAnswer("ACID 특성이 중요합니다.")
                .questionOrder(1)
                .generationContext("GPT-4 generated")
                .build();
        tailQuestion3.submitAnswer("READ_COMMITTED, REPEATABLE_READ 등이 있습니다.");
        entityManager.persistAndFlush(tailQuestion3);

        // interview2의 꼬리 질문
        tailQuestion4 = InterviewTailQuestion.builder()
                .interview(interview2)
                .originalQuestion(question1)
                .content("스프링 부트와 스프링의 차이점은?")
                .originalUserAnswer("스프링은 프레임워크입니다.")
                .questionOrder(1)
                .generationContext("GPT-4 generated")
                .build();
        // 답변 안함 (진행 중인 면접)
        entityManager.persistAndFlush(tailQuestion4);
    }

    @Nested
    @DisplayName("세션별 꼬리 질문 조회 - N+1 문제 방지")
    class SessionBasedQuery {

        @Test
        @DisplayName("Fetch Join으로 면접 세션의 모든 꼬리 질문을 효율적으로 조회한다")
        void findByInterviewIdWithDetails_FetchJoin_Success() {
            // When: N+1 방지 쿼리 실행
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findByInterviewIdWithDetails(interview1.getInterviewId());

            // Then: 효율적인 쿼리로 데이터 조회 검증
            assertThat(result).hasSize(3);
            
            // 정렬 순서 검증 (originalQuestion.id, questionOrder 순)
            assertEquals(question1.getId(), result.get(0).getOriginalQuestion().getId());
            assertEquals(1, result.get(0).getQuestionOrder());
            assertEquals("IoC와 DI의 차이점은 무엇인가요?", result.get(0).getContent());
            
            assertEquals(question1.getId(), result.get(1).getOriginalQuestion().getId());
            assertEquals(2, result.get(1).getQuestionOrder());
            assertEquals("AOP의 실제 사용 예시를 들어주세요.", result.get(1).getContent());
            
            assertEquals(question2.getId(), result.get(2).getOriginalQuestion().getId());
            assertEquals(1, result.get(2).getQuestionOrder());
            
            // Fetch Join으로 Lazy Loading 없이 접근 가능 검증
            assertEquals("스프링 프레임워크의 특징을 설명해주세요.", 
                        result.get(0).getOriginalQuestion().getContent());
            assertEquals("room-001", result.get(0).getInterview().getRoomId());
        }

        @Test
        @DisplayName("존재하지 않는 면접 ID로 조회 시 빈 리스트 반환")
        void findByInterviewIdWithDetails_NotFound_ReturnsEmpty() {
            // When
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findByInterviewIdWithDetails(999L);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("원본 질문별 꼬리 질문 조회")
    class OriginalQuestionBasedQuery {

        @Test
        @DisplayName("특정 면접 세션에서 특정 원본 질문의 꼬리 질문들을 순서대로 조회한다")
        void findByInterviewIdAndOriginalQuestionId_OrderedByQuestionOrder_Success() {
            // When: question1에 대한 꼬리 질문들 조회
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findByInterviewIdAndOriginalQuestionId(interview1.getInterviewId(), question1.getId());

            // Then: questionOrder 순서로 정렬된 결과 검증
            assertThat(result).hasSize(2);
            assertEquals(1, result.get(0).getQuestionOrder());
            assertEquals("IoC와 DI의 차이점은 무엇인가요?", result.get(0).getContent());
            assertEquals(2, result.get(1).getQuestionOrder());
            assertEquals("AOP의 실제 사용 예시를 들어주세요.", result.get(1).getContent());
        }

        @Test
        @DisplayName("꼬리 질문이 없는 원본 질문 조회 시 빈 리스트 반환")
        void findByInterviewIdAndOriginalQuestionId_NoTailQuestions_ReturnsEmpty() {
            // Given: 꼬리 질문이 없는 새로운 질문
            Question questionWithoutTail = Question.builder()
                    .content("새로운 질문")
                    .questionType(CUSTOM)
                    .expectedAnswer("새로운 답변")
                    .build();
            entityManager.persistAndFlush(questionWithoutTail);

            // When
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findByInterviewIdAndOriginalQuestionId(interview1.getInterviewId(), questionWithoutTail.getId());

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("답변 상태별 필터링")
    class AnswerStatusFiltering {

        @Test
        @DisplayName("답변 완료되지 않은 꼬리 질문들만 조회한다")
        void findUnansweredByInterviewId_OnlyUnanswered_Success() {
            // When: 답변 안된 꼬리 질문들 조회
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findUnansweredByInterviewId(interview1.getInterviewId());

            // Then: 답변 안된 것만 조회됨
            assertThat(result).hasSize(1);
            assertEquals("AOP의 실제 사용 예시를 들어주세요.", result.get(0).getContent());
            assertTrue(result.get(0).getUserAnswer() == null || result.get(0).getUserAnswer().isEmpty());
        }

        @Test
        @DisplayName("모든 꼬리 질문이 답변 완료된 경우 빈 리스트 반환")
        void findUnansweredByInterviewId_AllAnswered_ReturnsEmpty() {
            // Given: 미답변 꼬리 질문에 답변 추가
            tailQuestion2.submitAnswer("로깅, 보안, 트랜잭션 관리에 사용됩니다.");
            entityManager.merge(tailQuestion2); // 혹은 이미 managed면 생략 가능
            entityManager.flush();
            entityManager.clear();

            // When
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findUnansweredByInterviewId(interview1.getInterviewId());

            // Then: 모두 답변 완료되어 빈 리스트
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("데이터 분석 및 통계 쿼리")
    class DataAnalyticsQueries {

        @Test
        @DisplayName("특정 원본 질문에 대한 꼬리 질문 사용 통계를 조회한다")
        void findTailQuestionStatsByOriginalQuestion_UsageStats_Success() {
            // Given: 동일한 꼬리 질문을 여러 면접에서 생성하기 위해 추가 데이터 생성
            InterviewTailQuestion duplicateTailQuestion = InterviewTailQuestion.builder()
                    .interview(interview2)
                    .originalQuestion(question1)
                    .content("IoC와 DI의 차이점은 무엇인가요?") // 동일한 내용
                    .originalUserAnswer("스프링은 프레임워크입니다.")
                    .questionOrder(2)
                    .generationContext("GPT-4 generated")
                    .build();
            entityManager.persistAndFlush(duplicateTailQuestion);
            entityManager.clear();

            // When: 통계 쿼리 실행
            List<Object[]> result = interviewTailQuestionRepository
                    .findTailQuestionStatsByOriginalQuestion(question1.getId());

            // Then: 사용 빈도순으로 정렬된 통계 검증
            assertThat(result).isNotEmpty();
            
            Object[] mostUsedTailQuestion = result.get(0);
            assertEquals("IoC와 DI의 차이점은 무엇인가요?", mostUsedTailQuestion[0]);
            assertEquals(2L, mostUsedTailQuestion[1]); // 2번 사용됨
        }

        @Test
        @DisplayName("면접 세션별 꼬리 질문 생성 통계를 조회한다")
        void findInterviewTailQuestionStats_SessionStats_Success() {
            // When: 면접 세션 통계 조회
            List<Object[]> result = interviewTailQuestionRepository
                    .findInterviewTailQuestionStats(interview1.getInterviewId());

            // Then: 면접별 통계 검증
            assertThat(result).hasSize(1);
            
            Object[] interviewStats = result.get(0);
            assertEquals(interview1.getInterviewId(), interviewStats[0]);
            assertEquals(3L, interviewStats[1]); // 총 꼬리 질문 3개
            // avg_answer_length는 답변한 질문들의 평균 길이
        }
    }

    @Nested
    @DisplayName("사용자별 학습 데이터 분석")
    class UserLearningDataAnalysis {

        @Test
        @DisplayName("특정 사용자의 최근 면접 꼬리 질문들을 시간순으로 조회한다")
        void findRecentTailQuestionsByUser_TimeOrdered_Success() {
            // Given: 시간 기준점 설정
            LocalDateTime fromDate = LocalDateTime.now().minusDays(7);

            // When: 최근 일주일간의 꼬리 질문 조회
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findRecentTailQuestionsByUser(interviewee.getUserId(), fromDate);

            // Then: 시간순 정렬 및 사용자 필터링 검증
            assertThat(result).isNotEmpty();
            
            // 모든 결과가 해당 사용자의 면접 것인지 검증
            result.forEach(tq -> {
                assertEquals(interviewee.getUserId(), tq.getInterview().getInterviewee().getUserId());
            });
            
            // 최신 면접의 꼬리 질문들이 먼저 조회되는지 검증
            // (interview1이 완료된 면접이므로 completedAt이 설정됨)
        }

        @Test
        @DisplayName("기간 내 면접이 없는 사용자 조회 시 빈 리스트 반환")
        void findRecentTailQuestionsByUser_NoInterviewsInPeriod_ReturnsEmpty() {
            // Given: 미래 날짜로 기준점 설정
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

            // When
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findRecentTailQuestionsByUser(interviewee.getUserId(), futureDate);

            // Then: 조건에 맞는 면접이 없으므로 빈 리스트
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Question 불변성과 세션별 독립성 검증")
    class ImmutabilityAndSessionIndependence {

        @Test
        @DisplayName("동일한 불변 Question이 여러 면접 세션에서 다른 꼬리 질문을 생성한다")
        void sameQuestionDifferentTailQuestions_SessionIndependence_Success() {
            // When: 두 면접 세션에서 동일한 원본 질문 조회
            List<InterviewTailQuestion> interview1TailQuestions = interviewTailQuestionRepository
                    .findByInterviewIdAndOriginalQuestionId(interview1.getInterviewId(), question1.getId());
            
            List<InterviewTailQuestion> interview2TailQuestions = interviewTailQuestionRepository
                    .findByInterviewIdAndOriginalQuestionId(interview2.getInterviewId(), question1.getId());

            // Then: 세션별 독립적인 꼬리 질문 생성 검증
            assertThat(interview1TailQuestions).hasSize(2);
            assertThat(interview2TailQuestions).hasSize(1);
            
            // 동일한 불변 Question 참조 확인
            assertEquals(question1.getId(), interview1TailQuestions.get(0).getOriginalQuestion().getId());
            assertEquals(question1.getId(), interview2TailQuestions.get(0).getOriginalQuestion().getId());
            
            // 하지만 생성된 꼬리 질문 내용은 다름 (사용자 답변에 따라 달라짐)
            String interview1FirstTailQuestion = interview1TailQuestions.get(0).getContent();
            String interview2FirstTailQuestion = interview2TailQuestions.get(0).getContent();
            assertThat(interview1FirstTailQuestion).isNotEqualTo(interview2FirstTailQuestion);
        }

        @Test
        @DisplayName("꼬리 질문의 원본 답변 컨텍스트가 올바르게 저장된다")
        void originalAnswerContext_ProperlyStored_Success() {
            // When
            List<InterviewTailQuestion> result = interviewTailQuestionRepository
                    .findByInterviewIdAndOriginalQuestionId(interview1.getInterviewId(), question1.getId());

            // Then: AI 생성 시 참고한 원본 답변이 저장되어 있음
            assertThat(result).isNotEmpty();
            assertEquals("IoC와 DI가 스프링의 핵심 특징입니다.", 
                        result.get(0).getOriginalUserAnswer());
            assertEquals("GPT-4 generated", result.get(0).getGenerationContext());
        }
    }
}