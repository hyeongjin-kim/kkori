package com.kkori.performance;

import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.User;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ScalabilityTest {

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
    @DisplayName("점진적 규모 확장 성능 테스트")
    void 점진적_규모_확장_성능테스트() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📈 점진적 규모 확장 성능 테스트 시작");
        System.out.println("=".repeat(80));
        
        // 테스트 사용자 생성
        User testUser = User.builder()
                .sub("scalability-test-user")
                .nickname("확장성테스트유저")
                .build();
        testUser = userRepository.save(testUser);
        
        // 다양한 규모로 테스트
        int[] testSizes = {100, 500, 1000, 2000, 5000};
        
        for (int size : testSizes) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🔍 " + size + "개 데이터 성능 테스트");
            System.out.println("=".repeat(60));
            
            testWithDataSize(testUser, size);
            
            // 메모리 정리
            System.gc();
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        }
        
        System.out.println("\n🎉 점진적 확장 테스트 완료!");
    }
    
    private void testWithDataSize(User testUser, int dataSize) {
        // === 데이터 생성 단계 ===
        System.out.println("📊 " + dataSize + "개 테스트 데이터 생성 중...");
        
        long creationStart = System.currentTimeMillis();
        QuestionSet questionSet = createTestData(testUser, dataSize);
        long creationTime = System.currentTimeMillis() - creationStart;
        
        System.out.println("✅ 데이터 생성 완료: " + creationTime + "ms");
        
        // 강제 플러시 및 캐시 클리어
        entityManager.flush();
        entityManager.clear();
        
        // === 성능 테스트 단계 ===
        performanceTestForSize(questionSet, dataSize);
        
        // === 메모리 상태 출력 ===
        printMemoryStatus();
        
        // 테스트 데이터 정리
        cleanupTestData(questionSet);
    }
    
    private QuestionSet createTestData(User testUser, int size) {
        // 질문세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .title("확장성 테스트 질문세트 (" + size + "개)")
                .description(size + "개 데이터로 성능을 테스트하는 질문세트")
                .ownerUserId(testUser)
                .isPublic(true)
                .versionNumber(1)
                .build();
        questionSet = questionSetRepository.save(questionSet);
        
        // 질문 및 매핑 생성
        List<Question> questions = new ArrayList<>();
        List<QuestionSetQuestionMap> maps = new ArrayList<>();
        
        int batchSize = Math.min(100, size / 10); // 적응적 배치 크기
        
        for (int i = 1; i <= size; i++) {
            // 질문 생성
            Question question = Question.defaultBuilder()
                    .content("확장성 테스트 질문 " + i + " (총 " + size + "개 중)")
                    .expectedAnswer("확장성 테스트 답변 " + i + " - 데이터 크기별 성능 차이를 확인하기 위한 테스트입니다.")
                    .build();
            questions.add(question);
            
            // 배치 단위로 저장
            if (i % batchSize == 0) {
                List<Question> savedQuestions = questionRepository.saveAll(questions);
                
                // 매핑 생성
                for (int j = 0; j < savedQuestions.size(); j++) {
                    QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                            .questionSet(questionSet)
                            .question(savedQuestions.get(j))
                            .displayOrder(i - savedQuestions.size() + j + 1)
                            .build();
                    maps.add(map);
                }
                
                questionSetQuestionMapRepository.saveAll(maps);
                entityManager.flush();
                entityManager.clear();
                
                questions.clear();
                maps.clear();
            }
        }
        
        // 남은 데이터 처리
        if (!questions.isEmpty()) {
            List<Question> savedQuestions = questionRepository.saveAll(questions);
            for (int j = 0; j < savedQuestions.size(); j++) {
                QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                        .questionSet(questionSet)
                        .question(savedQuestions.get(j))
                        .displayOrder(size - savedQuestions.size() + j + 1)
                        .build();
                maps.add(map);
            }
            questionSetQuestionMapRepository.saveAll(maps);
        }
        
        return questionSet;
    }
    
    private void performanceTestForSize(QuestionSet questionSet, int expectedSize) {
        System.out.println("🚀 " + expectedSize + "개 데이터 성능 측정");
        
        // === 최적화된 쿼리 테스트 ===
        System.out.println("  📊 최적화된 쿼리 (FETCH JOIN):");
        
        Runtime.getRuntime().gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();
        
        List<QuestionSetQuestionMap> optimizedResult = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSet.getId());
        
        // 모든 데이터 접근
        long totalTextLength = 0;
        for (QuestionSetQuestionMap map : optimizedResult) {
            totalTextLength += map.getQuestion().getContent().length();
            totalTextLength += map.getQuestion().getExpectedAnswer().length();
        }
        
        long optimizedTime = System.currentTimeMillis() - startTime;
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long optimizedMemory = afterMemory - beforeMemory;
        
        System.out.println("    ⏱️  실행 시간: " + optimizedTime + "ms");
        System.out.println("    📄 조회된 객체: " + optimizedResult.size() + "개");
        System.out.println("    📝 처리된 텍스트: " + (totalTextLength / 1024) + "KB");
        System.out.println("    💾 메모리 사용: " + formatBytes(optimizedMemory));
        if (optimizedTime > 0) {
            System.out.println("    🏃 처리 속도: " + (optimizedResult.size() * 1000 / optimizedTime) + " objects/sec");
        }
        System.out.println("    🎯 객체당 평균 메모리: " + formatBytes(Math.max(optimizedMemory, 0) / Math.max(optimizedResult.size(), 1)));
        
        // 캐시 클리어
        entityManager.clear();
        
        // === N+1 문제 샘플 테스트 ===
        System.out.println("  ⚠️  일반 쿼리 (샘플 10개):");
        
        Runtime.getRuntime().gc();
        beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();
        
        List<QuestionSetQuestionMap> normalResult = questionSetQuestionMapRepository
                .findByQuestionSetId(questionSet.getId());
        
        // 처음 10개만 데이터 접근
        long sampleTextLength = 0;
        int sampleSize = Math.min(10, normalResult.size());
        for (int i = 0; i < sampleSize; i++) {
            QuestionSetQuestionMap map = normalResult.get(i);
            sampleTextLength += map.getQuestion().getContent().length();
            sampleTextLength += map.getQuestion().getExpectedAnswer().length();
        }
        
        long normalTime = System.currentTimeMillis() - startTime;
        afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long normalMemory = afterMemory - beforeMemory;
        
        System.out.println("    ⏱️  샘플 실행 시간: " + normalTime + "ms");
        System.out.println("    📄 전체 객체 수: " + normalResult.size() + "개");
        
        // 전체 처리 시간 예상
        long projectedTime = normalTime * (normalResult.size() / Math.max(sampleSize, 1));
        System.out.println("    📈 예상 전체 처리 시간: " + projectedTime + "ms");
        
        // 성능 개선 계산
        if (projectedTime > optimizedTime && optimizedTime > 0) {
            double improvement = ((double)(projectedTime - optimizedTime) / projectedTime) * 100;
            System.out.println("    🎉 성능 개선: " + String.format("%.1f", improvement) + "%");
            double speedup = (double)projectedTime / optimizedTime;
            System.out.println("    ⚡ 속도 향상: " + String.format("%.1f", speedup) + "배");
        }
        
        // === 확장성 분석 ===
        System.out.println("  📊 확장성 분석:");
        if (expectedSize > 0 && optimizedTime > 0) {
            double timePerObject = (double)optimizedTime / expectedSize;
            System.out.println("    📌 객체당 처리 시간: " + String.format("%.3f", timePerObject) + "ms");
            
            // 50,000개 예상 시간 계산
            double projected50k = timePerObject * 50000;
            System.out.println("    🔮 50,000개 처리 예상 시간: " + String.format("%.0f", projected50k) + "ms");
        }
    }
    
    private void printMemoryStatus() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        System.out.println("  💾 메모리 상태:");
        System.out.println("    - 사용 중: " + formatBytes(usedMemory));
        System.out.println("    - 전체 할당: " + formatBytes(totalMemory));
        System.out.println("    - 최대 가용: " + formatBytes(maxMemory));
        System.out.println("    - 사용률: " + String.format("%.1f", (double)usedMemory / totalMemory * 100) + "%");
    }
    
    private void cleanupTestData(QuestionSet questionSet) {
        // 테스트 데이터 정리 (트랜잭션이므로 자동으로 롤백됨)
        System.out.println("  🧹 테스트 데이터 정리 완료");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}