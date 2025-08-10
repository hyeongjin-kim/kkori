package com.kkori.performance;

import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.User;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;

import org.junit.jupiter.api.Assertions;
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
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@ActiveProfiles("test")
public class LargeScalePerformanceTest {

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
    @DisplayName("5만개 데이터 성능 검증 - 단계적 접근")
    void 대용량_데이터_성능_검증() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔥 5만개 데이터 성능 검증 시작!");
        System.out.println("=".repeat(80));
        
        // === 1단계: 테스트 사용자 생성 ===
        System.out.println("\n📊 1단계: 테스트 사용자 생성");
        User testUser = User.builder()
                .sub("large-scale-test-user")
                .nickname("대용량테스트유저")
                .build();
        testUser = userRepository.save(testUser);
        System.out.println("✅ 테스트 사용자 생성 완료");
        
        // === 2단계: 질문세트 생성 (50개) ===
        System.out.println("\n📊 2단계: 질문세트 생성 (50개)");
        List<QuestionSet> questionSets = create50QuestionSets(testUser);
        System.out.println("✅ 50개 질문세트 생성 완료");
        
        // === 3단계: 대량 질문 생성 (50,000개) ===
        System.out.println("\n📊 3단계: 대량 질문 생성 (50,000개)");
        long questionCreationStart = System.currentTimeMillis();
        create50000Questions();
        long questionCreationTime = System.currentTimeMillis() - questionCreationStart;
        System.out.println("✅ 50,000개 질문 생성 완료: " + questionCreationTime + "ms");
        
        // === 4단계: 질문세트-질문 매핑 (50,000개) ===
        System.out.println("\n📊 4단계: 질문세트-질문 매핑 생성 (50,000개)");
        long mappingStart = System.currentTimeMillis();
        create50000Mappings(questionSets);
        long mappingTime = System.currentTimeMillis() - mappingStart;
        System.out.println("✅ 50,000개 매핑 생성 완료: " + mappingTime + "ms");
        
        // 강제 플러시 및 캐시 클리어
        entityManager.flush();
        entityManager.clear();
        
        System.out.println("\n📈 데이터 생성 완료 요약:");
        System.out.println("  - 질문세트: 50개");
        System.out.println("  - 질문: 50,000개");
        System.out.println("  - 매핑: 50,000개");
        System.out.println("  - 총 생성 시간: " + (questionCreationTime + mappingTime) + "ms");
        
        // === 5단계: 성능 테스트 실행 ===
        performLargeScalePerformanceTest(questionSets);
        
        System.out.println("\n🎉 대용량 성능 검증 완료!");
        System.out.println("=".repeat(80));
    }
    
    private List<QuestionSet> create50QuestionSets(User testUser) {
        List<QuestionSet> questionSets = new ArrayList<>();
        
        for (int i = 1; i <= 50; i++) {
            QuestionSet questionSet = QuestionSet.builder()
                    .title("대용량 테스트 질문세트 " + i)
                    .description("5만개 데이터 성능 테스트용 질문세트입니다.")
                    .ownerUserId(testUser)
                    .isPublic(i % 2 == 0)  // 절반은 공개
                    .versionNumber(1)
                    .build();
            questionSets.add(questionSet);
        }
        
        // 배치로 저장
        questionSets = questionSetRepository.saveAll(questionSets);
        return questionSets;
    }
    
    private void create50000Questions() {
        System.out.println("  진행률 추적: ");
        
        List<Question> batch = new ArrayList<>();
        int batchSize = 1000; // 메모리 고려하여 배치 크기 설정
        
        for (int i = 1; i <= 50000; i++) {
            Question question = Question.defaultBuilder()
                    .content(generateRealisticQuestionContent(i))
                    .expectedAnswer(generateRealisticAnswerContent(i))
                    .build();
            batch.add(question);
            
            // 배치 처리
            if (i % batchSize == 0) {
                questionRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear(); // 메모리 해제
                batch.clear();
                
                // 진행률 표시
                int progress = (i * 100) / 50000;
                if (i % 5000 == 0) {
                    System.out.println("    " + i + "/50,000 완료 (" + progress + "%)");
                }
            }
        }
        
        // 남은 데이터 저장
        if (!batch.isEmpty()) {
            questionRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }
    }
    
    private void create50000Mappings(List<QuestionSet> questionSets) {
        System.out.println("  진행률 추적: ");
        
        List<Question> allQuestions = questionRepository.findAll();
        System.out.println("    실제 생성된 질문 수: " + allQuestions.size());
        
        List<QuestionSetQuestionMap> batch = new ArrayList<>();
        int batchSize = 1000;
        int mapIndex = 0;
        
        // 각 질문세트당 1000개씩 질문 할당
        for (QuestionSet questionSet : questionSets) {
            int questionsPerSet = 1000; // 50개 세트 × 1000개 = 50,000개
            
            for (int j = 0; j < questionsPerSet && mapIndex < allQuestions.size(); j++, mapIndex++) {
                Question question = allQuestions.get(mapIndex);
                
                QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                        .questionSet(questionSet)
                        .question(question)
                        .displayOrder(j + 1)
                        .build();
                batch.add(map);
                
                // 배치 처리
                if (batch.size() >= batchSize) {
                    questionSetQuestionMapRepository.saveAll(batch);
                    entityManager.flush();
                    entityManager.clear();
                    batch.clear();
                    
                    // 진행률 표시
                    if (mapIndex % 5000 == 0) {
                        int progress = (mapIndex * 100) / 50000;
                        System.out.println("    " + mapIndex + "/50,000 매핑 완료 (" + progress + "%)");
                    }
                }
            }
        }
        
        // 남은 데이터 저장
        if (!batch.isEmpty()) {
            questionSetQuestionMapRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }
    }
    
    private void performLargeScalePerformanceTest(List<QuestionSet> questionSets) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 대용량 성능 테스트 시작");
        System.out.println("=".repeat(60));
        
        // 테스트할 질문세트 선택 (첫 번째, 중간, 마지막)
        QuestionSet[] testSets = {
            questionSets.get(0),                    // 첫 번째
            questionSets.get(questionSets.size()/2), // 중간
            questionSets.get(questionSets.size()-1)  // 마지막
        };
        
        String[] testNames = {"첫 번째", "중간", "마지막"};
        
        for (int i = 0; i < testSets.length; i++) {
            System.out.println("\n📊 " + testNames[i] + " 질문세트 성능 테스트 (1000개 질문)");
            testSingleQuestionSetPerformance(testSets[i], testNames[i]);
            
            // 메모리 정리
            System.gc();
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        
        // === 전체 통계 테스트 ===
        System.out.println("\n📈 전체 데이터베이스 통계");
        printDatabaseStatistics();
    }
    
    private void testSingleQuestionSetPerformance(QuestionSet questionSet, String testName) {
        // === 최적화된 쿼리 테스트 ===
        System.out.println("  🚀 최적화된 쿼리 (FETCH JOIN)");
        
        Runtime.getRuntime().gc();
        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();
        
        List<QuestionSetQuestionMap> optimizedResult = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSet.getId());
        
        // 실제 데이터 접근
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
        System.out.println("    🏃 처리 속도: " + (optimizedResult.size() * 1000 / Math.max(optimizedTime, 1)) + " objects/sec");
        
        // 캐시 클리어
        entityManager.clear();
        
        // === 일반 쿼리 테스트 (샘플만) ===
        System.out.println("  ⚡ 일반 쿼리 (처음 10개만 테스트)");
        
        Runtime.getRuntime().gc();
        beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();
        
        List<QuestionSetQuestionMap> normalResult = questionSetQuestionMapRepository
                .findByQuestionSetId(questionSet.getId());
        
        // 처음 10개만 데이터 접근 (N+1 문제 시뮬레이션)
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
        
        System.out.println("    ⏱️  실행 시간 (10개 샘플): " + normalTime + "ms");
        System.out.println("    📄 전체 조회 객체: " + normalResult.size() + "개");
        System.out.println("    💾 메모리 사용: " + formatBytes(normalMemory));
        
        // 예상 성능 계산
        long projectedNormalTime = normalTime * (normalResult.size() / Math.max(sampleSize, 1));
        System.out.println("    📊 전체 처리 예상 시간: " + projectedNormalTime + "ms");
        
        if (optimizedTime < projectedNormalTime) {
            double improvement = ((double)(projectedNormalTime - optimizedTime) / projectedNormalTime) * 100;
            System.out.println("    🎉 최적화로 " + String.format("%.1f", improvement) + "% 성능 향상!");
        }
    }
    
    private void printDatabaseStatistics() {
        long questionCount = questionRepository.count();
        long questionSetCount = questionSetRepository.count();
        long mappingCount = questionSetQuestionMapRepository.count();
        
        System.out.println("  📋 데이터베이스 통계:");
        System.out.println("    - 질문 수: " + questionCount + "개");
        System.out.println("    - 질문세트 수: " + questionSetCount + "개");
        System.out.println("    - 매핑 수: " + mappingCount + "개");
        System.out.println("    - 평균 질문/세트: " + (mappingCount / Math.max(questionSetCount, 1)) + "개");
    }
    
    private String generateRealisticQuestionContent(int index) {
        String[] templates = {
            "기술면접 질문 %d: 자바의 메모리 구조에 대해 설명하고, 힙과 스택의 차이점을 구체적인 예시와 함께 설명해주세요.",
            "면접질문 %d: 스프링 프레임워크의 IoC 컨테이너가 어떻게 동작하는지 설명하고, 의존성 주입의 장점에 대해 말씀해주세요.",
            "기술질문 %d: RESTful API 설계 원칙에 대해 설명하고, 실제 프로젝트에서 어떻게 적용했는지 경험을 공유해주세요.",
            "면접문제 %d: 데이터베이스 정규화에 대해 설명하고, 1NF부터 3NF까지의 차이점과 실무에서의 활용 예시를 들어주세요.",
            "기술문제 %d: 알고리즘의 시간복잡도와 공간복잡도에 대해 설명하고, Big-O 표기법을 사용한 분석 방법을 예시와 함께 설명해주세요."
        };
        
        int templateIndex = index % templates.length;
        return String.format(templates[templateIndex], index);
    }
    
    private String generateRealisticAnswerContent(int index) {
        String[] templates = {
            "답변 %d: 자바 메모리는 크게 힙(Heap)과 스택(Stack)으로 구분됩니다. 힙 영역은 객체와 배열이 저장되는 공간으로 가비지 컬렉션의 대상이 되며, 스택 영역은 메서드 호출과 지역변수가 저장되는 공간으로 LIFO 구조를 가집니다.",
            "답변 %d: 스프링의 IoC 컨테이너는 객체의 생성과 의존관계 설정을 개발자 대신 처리합니다. 이를 통해 객체 간의 결합도를 낮추고, 테스트 용이성과 유지보수성을 향상시킬 수 있습니다.",
            "답변 %d: RESTful API는 Representational State Transfer 아키텍처 스타일을 따르는 API로, 자원을 URI로 표현하고 HTTP 메서드를 통해 자원에 대한 행위를 정의합니다. 실제 프로젝트에서는 명사형 URI 사용, 적절한 HTTP 상태코드 활용 등을 적용했습니다.",
            "답변 %d: 데이터베이스 정규화는 데이터 중복을 최소화하고 데이터 무결성을 보장하기 위한 과정입니다. 1NF는 원자값 저장, 2NF는 부분함수 종속 제거, 3NF는 이행함수 종속을 제거하는 단계입니다.",
            "답변 %d: 시간복잡도는 알고리즘 실행 시간이 입력 크기에 따라 어떻게 증가하는지를 나타내며, 공간복잡도는 알고리즘이 사용하는 메모리 공간의 크기를 나타냅니다. Big-O 표기법으로 O(1), O(log n), O(n), O(n²) 등으로 표현합니다."
        };
        
        int templateIndex = index % templates.length;
        return String.format(templates[templateIndex], index);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}