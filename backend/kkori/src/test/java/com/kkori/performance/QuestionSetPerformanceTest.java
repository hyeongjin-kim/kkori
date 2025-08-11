package com.kkori.performance;

import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.User;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QuestionSetPerformanceTest {

    @Autowired
    private QuestionSetRepository questionSetRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionSetQuestionMapRepository questionSetQuestionMapRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private User testUser;
    private Long questionSetId;
    
    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .sub("test-user-sub")
                .nickname("테스트사용자")
                .build();
        testUser = userRepository.save(testUser);
        
        // 테스트 데이터 생성
        createTestData();
    }
    
    void createTestData() {
        // 질문세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .title("성능 테스트용 질문세트")
                .description("대용량 데이터 성능 테스트")
                .ownerUserId(testUser)
                .isPublic(false)
                .versionNumber(1)
                .build();
        questionSet = questionSetRepository.save(questionSet);
        questionSetId = questionSet.getId();
        
        // 100개의 질문 생성 (실제 환경 시뮬레이션)
        List<Question> questions = new ArrayList<>();
        List<QuestionSetQuestionMap> maps = new ArrayList<>();
        
        for (int i = 1; i <= 100; i++) {
            Question question = Question.defaultBuilder()
                    .content("테스트 질문 내용 " + i + " - 이것은 성능 테스트를 위한 상당히 긴 질문 내용입니다. 실제 면접에서 나올 수 있는 질문들을 시뮬레이션하기 위해 충분한 길이의 텍스트를 포함합니다.")
                    .expectedAnswer("테스트 답변 내용 " + i + " - 이것은 해당 질문에 대한 기대되는 답변입니다. 실제 답변과 유사한 길이를 시뮬레이션하기 위해 충분히 긴 텍스트를 포함합니다. 기술적인 내용이나 경험담 등이 포함될 수 있습니다.")
                    .build();
            question = questionRepository.save(question);
            questions.add(question);
            
            QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                    .questionSet(questionSet)
                    .question(question)
                    .displayOrder(i)
                    .build();
            maps.add(map);
        }
        
        questionSetQuestionMapRepository.saveAll(maps);
        
        // 강제로 플러시하여 DB에 저장
        entityManager.flush();
        entityManager.clear();
    }
    
    @Test
    @DisplayName("최적화된 쿼리 성능 측정 - FETCH JOIN 사용")
    void 최적화된_쿼리_성능측정() {
        // Hibernate 통계 초기화
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch stopWatch = new StopWatch("최적화된 쿼리");
        stopWatch.start();
        
        // 최적화된 메서드 호출 (FETCH JOIN 사용)
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSetId);
        
        // 지연 로딩 트리거를 위해 실제 데이터 접근
        for (QuestionSetQuestionMap map : maps) {
            String content = map.getQuestion().getContent();
            String expectedAnswer = map.getQuestion().getExpectedAnswer();
            // 데이터 접근으로 지연 로딩 트리거
        }
        
        stopWatch.stop();
        
        // 결과 출력
        System.out.println("=== 최적화된 쿼리 성능 결과 ===");
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("실행된 SQL 개수: " + stats.getQueryExecutionCount());
        System.out.println("조회된 엔티티 개수: " + stats.getEntityLoadCount());
        System.out.println("2차 캐시 히트: " + stats.getSecondLevelCacheHitCount());
        System.out.println("로드된 QuestionSetQuestionMap 개수: " + maps.size());
    }
    
    @Test
    @DisplayName("메모리 사용량 측정")
    void 메모리_사용량_측정() {
        // GC 실행하여 정확한 측정
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 데이터 로딩
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSetId);
        
        // 실제 데이터 접근하여 객체 생성 완료
        for (QuestionSetQuestionMap map : maps) {
            map.getQuestion().getContent();
            map.getQuestion().getExpectedAnswer();
        }
        
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = afterMemory - beforeMemory;
        
        System.out.println("=== 메모리 사용량 측정 결과 ===");
        System.out.println("사용된 메모리: " + usedMemory + " bytes (" + (usedMemory / 1024) + " KB)");
        System.out.println("객체당 평균 메모리: " + (usedMemory / maps.size()) + " bytes");
        System.out.println("로드된 객체 수: " + maps.size());
    }
    
    @Test
    @DisplayName("대량 데이터 성능 테스트 (1000개)")
    void 대량_데이터_성능_테스트() {
        // 추가 데이터 생성 (총 1000개까지)
        QuestionSet questionSet = questionSetRepository.findById(questionSetId).orElse(null);
        
        for (int i = 101; i <= 1000; i++) {
            Question question = Question.defaultBuilder()
                    .content("대량 테스트 질문 " + i)
                    .expectedAnswer("대량 테스트 답변 " + i)
                    .build();
            question = questionRepository.save(question);
            
            QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                    .questionSet(questionSet)
                    .question(question)
                    .displayOrder(i)
                    .build();
            questionSetQuestionMapRepository.save(map);
        }
        
        entityManager.flush();
        entityManager.clear();
        
        // 성능 측정
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch stopWatch = new StopWatch("대량 데이터 조회");
        stopWatch.start();
        
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSetId);
        
        // 모든 데이터 접근
        int totalContent = 0;
        for (QuestionSetQuestionMap map : maps) {
            totalContent += map.getQuestion().getContent().length();
            totalContent += map.getQuestion().getExpectedAnswer().length();
        }
        
        stopWatch.stop();
        
        System.out.println("=== 대량 데이터(1000개) 성능 결과 ===");
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("실행된 SQL 개수: " + stats.getQueryExecutionCount());
        System.out.println("처리된 데이터 크기: " + totalContent + " characters");
        System.out.println("초당 처리 객체 수: " + (maps.size() * 1000 / stopWatch.getTotalTimeMillis()) + " objects/sec");
    }
    
    @Test
    @DisplayName("N+1 문제 시뮬레이션 (개별 조회)")
    void N플러스1_문제_시뮬레이션() {
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch stopWatch = new StopWatch("N+1 문제 시뮬레이션");
        stopWatch.start();
        
        // N+1 문제를 시뮬레이션 - 개별 조회
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetId(questionSetId);  // 기본 조회 (FETCH JOIN 없음)
        
        // 각 Question에 개별 접근 (지연 로딩 발생)
        for (QuestionSetQuestionMap map : maps) {
            String content = map.getQuestion().getContent();        // 추가 쿼리 발생
            String expectedAnswer = map.getQuestion().getExpectedAnswer();  // 데이터 접근
        }
        
        stopWatch.stop();
        
        System.out.println("=== N+1 문제 시뮬레이션 결과 ===");
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("실행된 SQL 개수: " + stats.getQueryExecutionCount());
        System.out.println("예상 쿼리 수: 1(QuestionSetQuestionMap) + " + maps.size() + "(Question) = " + (1 + maps.size()));
    }
    
    private Statistics getHibernateStatistics() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        return sessionFactory.getStatistics();
    }
}