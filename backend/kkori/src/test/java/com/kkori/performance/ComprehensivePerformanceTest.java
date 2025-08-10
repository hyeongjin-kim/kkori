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
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@ActiveProfiles("test")
public class ComprehensivePerformanceTest {

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
    private final List<Long> questionSetIds = new ArrayList<>();
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .sub("test-performance-user")
                .nickname("성능테스트유저")
                .build();
        testUser = userRepository.save(testUser);
        
        createRealisticTestData();
    }
    
    void createRealisticTestData() {
        System.out.println("🏗️ 실제 환경과 유사한 테스트 데이터 생성 중...");
        
        // 10개 질문세트 생성 (각각 다른 크기)
        int[] questionCounts = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
        
        for (int setIndex = 0; setIndex < questionCounts.length; setIndex++) {
            QuestionSet questionSet = QuestionSet.builder()
                    .title("실제 면접 질문세트 " + (setIndex + 1))
                    .description("실제 기업 면접에서 사용되는 질문들을 모은 세트입니다.")
                    .ownerUserId(testUser)
                    .isPublic(setIndex % 2 == 0)  // 절반은 공개
                    .versionNumber(1)
                    .build();
            questionSet = questionSetRepository.save(questionSet);
            questionSetIds.add(questionSet.getId());
            
            // 각 질문세트에 실제와 유사한 질문들 추가
            for (int i = 1; i <= questionCounts[setIndex]; i++) {
                Question question = Question.defaultBuilder()
                        .content(generateRealisticQuestion(setIndex + 1, i))
                        .expectedAnswer(generateRealisticAnswer(setIndex + 1, i))
                        .build();
                question = questionRepository.save(question);
                
                QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                        .questionSet(questionSet)
                        .question(question)
                        .displayOrder(i)
                        .build();
                questionSetQuestionMapRepository.save(map);
            }
        }
        
        entityManager.flush();
        entityManager.clear();
        
        int totalQuestions = questionSetIds.stream()
                .mapToInt(id -> questionCounts[questionSetIds.indexOf(id)])
                .sum();
        System.out.println("✅ 데이터 생성 완료: " + questionSetIds.size() + "개 질문세트, " + totalQuestions + "개 질문");
    }
    
    @Test
    @Transactional
    @DisplayName("최적화 전후 성능 비교 - 종합 테스트")
    void 최적화_전후_성능_종합비교() {
        System.out.println("\n🔥 최적화 전후 성능 비교 시작");
        System.out.println("=" .repeat(60));
        
        // 1. 최적화된 방법 (FETCH JOIN 사용)
        testOptimizedApproach();
        
        System.out.println("\n" + "-".repeat(60));
        
        // 2. N+1 문제 시뮬레이션 (개별 조회)
        testNPlusOneApproach();
        
        System.out.println("\n📈 성능 비교 완료!");
    }
    
    private void testOptimizedApproach() {
        System.out.println("🚀 최적화된 방법 (FETCH JOIN) 테스트");
        
        MemoryProfiler profiler = new MemoryProfiler();
        profiler.startProfiling();
        
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch stopWatch = new StopWatch("최적화된 방법");
        stopWatch.start();
        
        int totalProcessed = 0;
        long totalContentSize = 0;
        
        // 모든 질문세트에 대해 최적화된 조회 실행
        for (Long questionSetId : questionSetIds) {
            List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                    .findByQuestionSetIdWithDetails(questionSetId);
            
            // 실제 데이터 처리 시뮬레이션
            for (QuestionSetQuestionMap map : maps) {
                String content = map.getQuestion().getContent();
                String expectedAnswer = map.getQuestion().getExpectedAnswer();
                totalContentSize += content.length() + expectedAnswer.length();
                totalProcessed++;
            }
        }
        
        stopWatch.stop();
        MemoryProfiler.MemoryReport memoryReport = profiler.stopProfiling();
        
        // 결과 출력
        System.out.println("📊 최적화된 방법 결과:");
        System.out.println("  ⏱️  실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("  🔍 실행된 쿼리 수: " + stats.getQueryExecutionCount());
        System.out.println("  📄 처리된 객체 수: " + totalProcessed);
        System.out.println("  📝 처리된 텍스트 크기: " + (totalContentSize / 1024) + "KB");
        System.out.println("  🏃 처리 속도: " + (totalProcessed * 1000 / stopWatch.getTotalTimeMillis()) + " objects/sec");
        System.out.println("  💾 메모리 사용: " + formatBytes(memoryReport.heapDifference));
        System.out.println("  🎯 객체당 메모리: " + formatBytes(memoryReport.heapDifference / totalProcessed));
    }
    
    private void testNPlusOneApproach() {
        System.out.println("⚠️ N+1 문제 시뮬레이션 테스트");
        
        MemoryProfiler profiler = new MemoryProfiler();
        profiler.startProfiling();
        
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch stopWatch = new StopWatch("N+1 문제");
        stopWatch.start();
        
        int totalProcessed = 0;
        long totalContentSize = 0;
        
        // 모든 질문세트에 대해 N+1 문제 발생하는 조회 실행
        for (Long questionSetId : questionSetIds) {
            // 기본 조회 (FETCH JOIN 없음)
            List<QuestionSetQuestionMap> maps = questionSetQuestionMapRepository
                    .findByQuestionSetId(questionSetId);
            
            // 각 Question에 개별 접근 (지연 로딩으로 추가 쿼리 발생)
            for (QuestionSetQuestionMap map : maps) {
                String content = map.getQuestion().getContent();        // 추가 쿼리 가능
                String expectedAnswer = map.getQuestion().getExpectedAnswer();
                totalContentSize += content.length() + expectedAnswer.length();
                totalProcessed++;
            }
        }
        
        stopWatch.stop();
        MemoryProfiler.MemoryReport memoryReport = profiler.stopProfiling();
        
        // 결과 출력
        System.out.println("📊 N+1 문제 시뮬레이션 결과:");
        System.out.println("  ⏱️  실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
        System.out.println("  🔍 실행된 쿼리 수: " + stats.getQueryExecutionCount());
        System.out.println("  📄 처리된 객체 수: " + totalProcessed);
        System.out.println("  📝 처리된 텍스트 크기: " + (totalContentSize / 1024) + "KB");
        System.out.println("  🏃 처리 속도: " + (totalProcessed * 1000 / stopWatch.getTotalTimeMillis()) + " objects/sec");
        System.out.println("  💾 메모리 사용: " + formatBytes(memoryReport.heapDifference));
        System.out.println("  🎯 객체당 메모리: " + formatBytes(memoryReport.heapDifference / totalProcessed));
    }
    
    @Test
    @Transactional
    @DisplayName("대용량 단일 질문세트 성능 테스트")
    void 대용량_단일_질문세트_성능테스트() {
        System.out.println("\n🎯 대용량 단일 질문세트 성능 테스트");
        
        // 1000개 질문을 가진 대형 질문세트 생성
        QuestionSet largeQuestionSet = QuestionSet.builder()
                .title("대용량 질문세트 (1000개)")
                .description("성능 테스트용 대용량 데이터")
                .ownerUserId(testUser)
                .isPublic(true)
                .versionNumber(1)
                .build();
        largeQuestionSet = questionSetRepository.save(largeQuestionSet);
        
        // 1000개 질문 생성
        System.out.println("📚 1000개 질문 데이터 생성 중...");
        StopWatch creationWatch = new StopWatch("데이터 생성");
        creationWatch.start();
        
        List<Question> questions = new ArrayList<>();
        List<QuestionSetQuestionMap> maps = new ArrayList<>();
        
        for (int i = 1; i <= 1000; i++) {
            Question question = Question.defaultBuilder()
                    .content("대용량 테스트 질문 " + i + " - " + generateRealisticQuestion(1, i))
                    .expectedAnswer("대용량 테스트 답변 " + i + " - " + generateRealisticAnswer(1, i))
                    .build();
            questions.add(question);
            
            if (i % 100 == 0) {  // 배치 처리
                questionRepository.saveAll(questions);
                entityManager.flush();
                questions.clear();
            }
        }
        if (!questions.isEmpty()) {
            questionRepository.saveAll(questions);
        }
        
        // QuestionSetQuestionMap 배치 생성
        List<Question> allQuestions = questionRepository.findAll();
        for (int i = 0; i < Math.min(1000, allQuestions.size()); i++) {
            QuestionSetQuestionMap map = QuestionSetQuestionMap.builder()
                    .questionSet(largeQuestionSet)
                    .question(allQuestions.get(allQuestions.size() - 1000 + i))  // 마지막 1000개 사용
                    .displayOrder(i + 1)
                    .build();
            maps.add(map);
            
            if (maps.size() % 100 == 0) {  // 배치 처리
                questionSetQuestionMapRepository.saveAll(maps);
                entityManager.flush();
                maps.clear();
            }
        }
        if (!maps.isEmpty()) {
            questionSetQuestionMapRepository.saveAll(maps);
        }
        
        entityManager.flush();
        entityManager.clear();
        creationWatch.stop();
        
        System.out.println("✅ 데이터 생성 완료: " + creationWatch.getTotalTimeMillis() + "ms");
        
        // 성능 테스트 실행
        MemoryProfiler profiler = new MemoryProfiler();
        profiler.startProfiling();
        
        Statistics stats = getHibernateStatistics();
        stats.clear();
        
        StopWatch queryWatch = new StopWatch("대용량 조회");
        queryWatch.start();
        
        List<QuestionSetQuestionMap> result = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(largeQuestionSet.getId());
        
        // 모든 데이터 접근
        long totalTextLength = 0;
        for (QuestionSetQuestionMap map : result) {
            totalTextLength += map.getQuestion().getContent().length();
            totalTextLength += map.getQuestion().getExpectedAnswer().length();
        }
        
        queryWatch.stop();
        MemoryProfiler.MemoryReport report = profiler.stopProfiling();
        
        System.out.println("📊 대용량 성능 테스트 결과:");
        System.out.println("  📄 조회된 객체 수: " + result.size());
        System.out.println("  ⏱️  조회 시간: " + queryWatch.getTotalTimeMillis() + "ms");
        System.out.println("  🔍 실행된 쿼리 수: " + stats.getQueryExecutionCount());
        System.out.println("  📝 총 텍스트 길이: " + totalTextLength + " characters");
        System.out.println("  🏃 처리 속도: " + (result.size() * 1000 / queryWatch.getTotalTimeMillis()) + " objects/sec");
        System.out.println("  💾 메모리 사용: " + formatBytes(report.heapDifference));
        System.out.println("  🎯 객체당 메모리: " + formatBytes(report.heapDifference / result.size()));
        
        report.printDetailedReport();
    }
    
    private String generateRealisticQuestion(int setNumber, int questionNumber) {
        String[] templates = {
            "자기소개를 해주세요. 간단히 본인의 경력과 강점을 중심으로 말씀해주시기 바랍니다.",
            "이 회사에 지원한 이유는 무엇인가요? 우리 회사에서 어떤 일을 하고 싶으신지 구체적으로 설명해주세요.",
            "본인의 가장 큰 강점은 무엇이라고 생각하시나요? 실제 경험을 바탕으로 설명해주세요.",
            "가장 도전적이었던 프로젝트나 업무 경험에 대해 말씀해주세요. 어떤 어려움이 있었고 어떻게 해결하셨나요?",
            "팀워크에 대한 본인의 생각을 말씀해주시고, 팀 프로젝트에서의 본인의 역할에 대해 설명해주세요."
        };
        
        int templateIndex = (setNumber + questionNumber - 1) % templates.length;
        return templates[templateIndex] + " (세트" + setNumber + "-" + questionNumber + ")";
    }
    
    private String generateRealisticAnswer(int setNumber, int questionNumber) {
        String[] answerTemplates = {
            "안녕하세요. 저는 %d년차 개발자로서 주로 백엔드 개발을 담당해왔습니다. 특히 Spring Boot와 JPA를 활용한 웹 애플리케이션 개발에 강점을 가지고 있으며, 최근에는 마이크로서비스 아키텍처와 클라우드 환경에서의 개발 경험을 쌓고 있습니다.",
            "귀하의 회사가 추구하는 기술 혁신과 사용자 중심의 서비스 개발 철학이 제 가치관과 일치한다고 생각합니다. 특히 최근 출시하신 %s 서비스의 기술적 구조와 사용자 경험 설계가 매우 인상적이었으며, 이런 환경에서 제 경험을 활용하여 더 나은 서비스를 만들어가고 싶습니다.",
            "제 가장 큰 강점은 문제 해결 능력입니다. 복잡한 기술적 이슈가 발생했을 때 체계적으로 원인을 분석하고, 다양한 해결 방안을 검토한 후 최적의 솔루션을 찾아내는 것을 잘합니다. 실제로 이전 프로젝트에서 성능 이슈를 해결하여 응답 시간을 %d%% 개선한 경험이 있습니다.",
            "가장 도전적이었던 프로젝트는 레거시 시스템을 마이크로서비스로 전환하는 작업이었습니다. 기존 모놀리식 아키텍처의 복잡한 의존관계를 분석하고, 단계적으로 서비스를 분리해가는 과정에서 많은 어려움이 있었지만, 팀과의 긴밀한 협업을 통해 성공적으로 완료할 수 있었습니다.",
            "팀워크는 개발에서 매우 중요한 요소라고 생각합니다. 각자의 전문성을 인정하고 서로의 의견을 경청하며, 공통의 목표를 향해 협력하는 것이 좋은 결과를 만들어낸다고 믿습니다. 저는 주로 팀 내에서 기술적 가이드라인을 제시하고, 코드 리뷰를 통해 품질을 향상시키는 역할을 담당했습니다."
        };
        
        int templateIndex = (setNumber + questionNumber - 1) % answerTemplates.length;
        int experience = ThreadLocalRandom.current().nextInt(1, 8);
        String service = "혁신 플랫폼";
        int improvement = ThreadLocalRandom.current().nextInt(30, 90);
        
        return String.format(answerTemplates[templateIndex], experience, service, improvement);
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