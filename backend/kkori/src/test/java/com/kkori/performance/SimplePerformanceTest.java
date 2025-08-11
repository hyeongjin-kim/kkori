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
public class SimplePerformanceTest {

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
    
    @Test
    @Transactional
    @DisplayName("간단한 성능 검증 테스트")
    void 간단한_성능_검증() {
        System.out.println("\n🔥 실제 성능 검증 시작!");
        System.out.println("=" .repeat(50));
        
        // 1. 테스트 데이터 생성
        User testUser = User.builder()
                .sub("test-user")
                .nickname("테스트유저")
                .build();
        testUser = userRepository.save(testUser);
        
        QuestionSet questionSet = QuestionSet.builder()
                .title("성능 테스트 질문세트")
                .description("실제 성능 측정용")
                .ownerUserId(testUser)
                .isPublic(true)
                .versionNumber(1)
                .build();
        questionSet = questionSetRepository.save(questionSet);
        
        // 50개 질문 생성 (현실적인 크기)
        List<QuestionSetQuestionMap> maps = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Question question = Question.defaultBuilder()
                    .content("테스트 질문 " + i + " - 이것은 실제 면접 질문과 유사한 길이의 텍스트입니다. 기술적인 내용이나 업무 경험에 대한 질문일 수 있습니다.")
                    .expectedAnswer("테스트 답변 " + i + " - 이것은 해당 질문에 대한 기대 답변입니다. 실제 답변과 유사한 길이와 내용을 가지고 있으며, 기술적 설명이나 경험담이 포함될 수 있습니다.")
                    .build();
            question = questionRepository.save(question);
            
            QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                    .questionSet(questionSet)
                    .question(question)
                    .displayOrder(i)
                    .build();
            maps.add(map);
        }
        questionSetQuestionMapRepository.saveAll(maps);
        entityManager.flush();
        entityManager.clear();
        
        System.out.println("✅ 테스트 데이터 생성 완료: 50개 질문");
        
        // 2. 최적화된 방법 측정
        testOptimizedQuery(questionSet.getId());
        
        System.out.println("-" .repeat(50));
        
        // 3. N+1 문제 시뮬레이션
        testNPlusOneQuery(questionSet.getId());
        
        System.out.println("🎉 성능 검증 완료!");
    }
    
    private void testOptimizedQuery(Long questionSetId) {
        System.out.println("🚀 최적화된 쿼리 테스트 (FETCH JOIN)");
        
        // Hibernate 통계 초기화
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        // 메모리 측정 시작
        Runtime.getRuntime().gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // 최적화된 조회 실행
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSetId);
        
        // 실제 데이터 접근
        long totalTextSize = 0;
        for (QuestionSetQuestionMap map : maps) {
            String content = map.getQuestion().getContent();
            String expectedAnswer = map.getQuestion().getExpectedAnswer();
            totalTextSize += content.length() + expectedAnswer.length();
        }
        
        stopWatch.stop();
        
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = afterMemory - beforeMemory;
        
        // 결과 출력
        System.out.println("📊 최적화된 쿼리 결과:");
        System.out.println("  ⏱️  실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("  🔍 실행된 쿼리 수: " + stats.getQueryExecutionCount());
        System.out.println("  📄 조회된 객체 수: " + maps.size());
        System.out.println("  📝 처리된 텍스트 크기: " + (totalTextSize / 1024) + "KB");
        System.out.println("  💾 메모리 사용량: " + formatBytes(memoryUsed));
        System.out.println("  🎯 객체당 평균 메모리: " + formatBytes(memoryUsed / maps.size()));
    }
    
    private void testNPlusOneQuery(Long questionSetId) {
        System.out.println("⚠️ N+1 문제 시뮬레이션");
        
        // Hibernate 통계 초기화
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        // 메모리 측정 시작
        Runtime.getRuntime().gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // N+1 문제 시뮬레이션 - FETCH JOIN 없는 조회
        List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                .findByQuestionSetId(questionSetId);
        
        // 각 Question에 개별 접근 (지연 로딩 발생 가능)
        long totalTextSize = 0;
        for (QuestionSetQuestionMap map : maps) {
            String content = map.getQuestion().getContent();        // 지연 로딩 발생 가능
            String expectedAnswer = map.getQuestion().getExpectedAnswer();
            totalTextSize += content.length() + expectedAnswer.length();
        }
        
        stopWatch.stop();
        
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = afterMemory - beforeMemory;
        
        // 결과 출력
        System.out.println("📊 N+1 문제 시뮬레이션 결과:");
        System.out.println("  ⏱️  실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("  🔍 실행된 쿼리 수: " + stats.getQueryExecutionCount());
        System.out.println("  📄 조회된 객체 수: " + maps.size());
        System.out.println("  📝 처리된 텍스트 크기: " + (totalTextSize / 1024) + "KB");
        System.out.println("  💾 메모리 사용량: " + formatBytes(memoryUsed));
        System.out.println("  🎯 객체당 평균 메모리: " + formatBytes(memoryUsed / maps.size()));
        
        // 성능 차이 분석
        System.out.println("\n📈 예상 분석:");
        if (stats.getQueryExecutionCount() > 2) {
            System.out.println("  ⚡ N+1 문제 발생! 추가 쿼리가 실행되었습니다.");
        } else {
            System.out.println("  ✅ JPA 1차 캐시로 인해 추가 쿼리가 발생하지 않았습니다.");
        }
    }
    
    private Statistics getHibernateStatistics() {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        return sessionFactory.getStatistics();
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}