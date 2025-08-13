package com.kkori.dummy;

import com.kkori.entity.*;
import com.kkori.component.interview.RoomStatus;
import com.kkori.repository.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyDataService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionSetRepository questionSetRepository;
    private final QuestionSetQuestionMapRepository questionSetQuestionMapRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewRecordRepository interviewRecordRepository;
    private final AnswerRepository answerRepository;
    private final TagRepository tagRepository;
    private final QuestionSetTagRepository questionSetTagRepository;
    private final JdbcTemplate jdbcTemplate;

    public void createDummyData() {
        // 기존 data.sql 기반 로직 (주석처리)
//       try {
//            // 1. data.sql 실행 (30명 유저, 30개 질문세트 생성)
//            executeSqlFile("data.sql");
//            System.out.println("data.sql 실행 완료 - 기본 유저 및 질문세트 생성됨");
//
//            // 2. 첫 번째 유저와 질문세트를 사용해서 추가 데이터 생성
//            User firstUser = userRepository.findById(1L)
//                    .orElseThrow(() -> new RuntimeException("data.sql 실행 후 userId=1인 사용자가 존재하지 않습니다."));
//            QuestionSet firstQuestionSet = questionSetRepository.findById(1L)
//                    .orElseThrow(() -> new RuntimeException("data.sql 실행 후 questionSetId=1인 질문세트가 존재하지 않습니다."));
//
//            // 3. 기존 로직으로 질문과 면접 데이터 생성
//            createQuestionsAndInterviews(firstUser, firstQuestionSet);
//
//            System.out.println("하이브리드 더미 데이터 생성 완료!");
//            System.out.println("- data.sql: 30명 유저, 30개 질문세트");
//            System.out.println("- 추가 생성: 18개 질문, 4개 면접(완료 3개, 진행중 1개)");
//
//        } catch (Exception e) {
//            System.err.println("더미 데이터 생성 중 오류 발생: " + e.getMessage());
//            throw new RuntimeException("더미 데이터 생성 실패", e);
//        }

        // 원래 로직으로 복원
        // 1. 더미 유저 생성
        User dummyUser = User.builder()
                .sub("dummy_user_" + System.currentTimeMillis())
                .nickname("테스트유저")
                .build();
        userRepository.save(dummyUser);

        // 2. 태그를 먼저 생성
        createBasicTags();

        // 3. 질문 세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(dummyUser)
                .title("백엔드 개발자 면접 질문 세트")
                .description("Java/Spring 백엔드 개발자를 위한 기본 면접 질문 모음입니다.")
                .versionNumber(1)
                .isPublic(true)
                .build();
        questionSet = questionSetRepository.saveAndFlush(questionSet);

        // 4. 질문과 면접 데이터 생성
        createQuestionsAndInterviews(dummyUser, questionSet);

        // 5. 추가 유저 30명과 질문 세트 30개 생성
        createAdditionalUsersAndQuestionSets();

        System.out.println("더미 데이터 생성 완료!");
        System.out.println("생성된 유저 ID: " + dummyUser.getUserId());
        System.out.println("생성된 질문 세트 ID: " + questionSet.getId());
    }

    private void createQuestionsAndInterviews(User user, QuestionSet questionSet) {
        // 질문들 생성 (기본 질문 + 꼬리 질문)
        List<Question> questions = new ArrayList<>();

        // DEFAULT 타입 질문들 (기본 면접 질문) - 10개
        Question defaultQ1 = Question.createDefault("자바의 특징에 대해 설명해주세요.", "객체지향, 플랫폼 독립성, 가비지 컬렉션 등");
        Question defaultQ2 = Question.createDefault("스프링 프레임워크의 핵심 개념은 무엇인가요?", "IoC, DI, AOP, PSA");
        Question defaultQ3 = Question.createDefault("본인의 강점과 약점을 말씀해주세요.", "상황에 맞는 개인적 답변");
        Question defaultQ4 = Question.createDefault("이 회사에 지원한 동기는 무엇인가요?", "회사 비전과 개인 목표의 일치");
        Question defaultQ5 = Question.createDefault("팀워크에서 중요하게 생각하는 것은?", "소통, 협력, 상호 존중");
        Question defaultQ6 = Question.createDefault("객체지향 프로그래밍의 4가지 특성을 설명해주세요.", "캡슐화, 상속, 다형성, 추상화");
        Question defaultQ7 = Question.createDefault("데이터베이스의 정규화가 무엇인지 설명해주세요.", "데이터 중복을 최소화하고 일관성을 유지하는 과정");
        Question defaultQ8 = Question.createDefault("HTTP와 HTTPS의 차이점은 무엇인가요?", "HTTPS는 SSL/TLS를 통한 암호화된 HTTP");
        Question defaultQ9 = Question.createDefault("본인이 가장 자신있는 기술 스택은 무엇인가요?", "개인의 경험과 전문성에 따른 답변");
        Question defaultQ10 = Question.createDefault("향후 5년간의 커리어 계획을 말씀해주세요.", "개인의 성장 목표와 비전");

        questions.add(defaultQ1);
        questions.add(defaultQ2);
        questions.add(defaultQ3);
        questions.add(defaultQ4);
        questions.add(defaultQ5);
        questions.add(defaultQ6);
        questions.add(defaultQ7);
        questions.add(defaultQ8);
        questions.add(defaultQ9);
        questions.add(defaultQ10);

        // CUSTOM 타입 질문들 (면접관이 생성한 질문)
        questions.add(Question.createCustom("JPA와 Hibernate의 차이점은?"));
        questions.add(Question.createCustom("REST API 설계 원칙을 설명하세요."));
        questions.add(Question.createCustom("데이터베이스 트랜잭션의 ACID 특성은?"));

        // 기본 질문들 먼저 저장 (TAIL 질문의 parent 참조를 위해)
        questionRepository.saveAll(questions);

        // TAIL 타입 질문들 (기본 질문에 대한 꼬리 질문)
        List<Question> tailQuestions = new ArrayList<>();
        tailQuestions.add(Question.createTail("그렇다면 가비지 컬렉션의 동작 원리에 대해 더 자세히 설명해주세요.", defaultQ1));
        tailQuestions.add(Question.createTail("실제 프로젝트에서 DI를 어떻게 활용해보셨나요?", defaultQ2));
        tailQuestions.add(Question.createTail("그 강점을 이 회사에서 어떻게 발휘할 수 있을까요?", defaultQ3));
        tailQuestions.add(Question.createTail("우리 회사의 어떤 부분이 가장 매력적이었나요?", defaultQ4));
        tailQuestions.add(Question.createTail("팀에서 갈등이 생겼을 때는 어떻게 해결하시나요?", defaultQ5));

        // 꼬리 질문들 저장
        questionRepository.saveAll(tailQuestions);

        // 전체 질문 목록에 꼬리 질문들 추가
        questions.addAll(tailQuestions);

        // 질문 세트와 기존 태그들 매핑 (태그는 이미 생성됨)
        linkQuestionSetToExistingTags(questionSet);

        // 질문 세트와 질문들 매핑
        List<QuestionSetQuestionMap> questionMaps = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.builder()
                    .questionSet(questionSet)
                    .question(questions.get(i))
                    .displayOrder(i + 1)
                    .build();
            questionMaps.add(questionMap);
        }
        questionSetQuestionMapRepository.saveAll(questionMaps);

        // InterviewRecord 더미 데이터 생성
        createInterviewRecords(user, questionSet, questions);
    }

    private void createInterviewRecords(User mainUser, QuestionSet questionSet, List<Question> questions) {
        // 전달받은 사용자 사용

        // 완료된 면접 데이터 생성 (3개) - userId=1이 면접관이자 면접자
        for (int interviewNum = 1; interviewNum <= 3; interviewNum++) {
            // Interview 생성
            Interview interview = Interview.builder()
                    .interviewer(mainUser)    // userId=1
                    .interviewee(mainUser)    // userId=1 (동일 사용자)
                    .usedQuestionSet(questionSet)
                    .roomId("room_" + interviewNum + "_" + System.currentTimeMillis())
                    .build();
            interview.complete(); // 완료 상태로 설정
            interviewRepository.save(interview);

            // 각 면접마다 논리적인 순서로 10개의 질문-답변 기록 생성
            List<Question> logicalQuestions = new ArrayList<>();
            
            // 1-2번: 자바 질문 + 꼬리질문
            logicalQuestions.add(questions.get(0));  // DEFAULT: 자바의 특징
            logicalQuestions.add(Question.createTail("그렇다면 가비지 컬렉션의 동작 원리에 대해 더 자세히 설명해주세요.", questions.get(0)));
            
            // 3-4번: 스프링 질문 + 꼬리질문  
            logicalQuestions.add(questions.get(1));  // DEFAULT: 스프링 개념
            logicalQuestions.add(Question.createTail("실제 프로젝트에서 DI를 어떻게 활용해보셨나요?", questions.get(1)));
            
            // 5-6번: JPA 질문 + 꼬리질문
            logicalQuestions.add(questions.get(10));  // CUSTOM: JPA와 Hibernate
            logicalQuestions.add(Question.createTail("JPA를 실제 프로젝트에서 사용하면서 어려웠던 점은 무엇인가요?", questions.get(10)));
            
            // 7-8번: REST API 질문 + 꼬리질문
            logicalQuestions.add(questions.get(11));  // CUSTOM: REST API 설계
            logicalQuestions.add(Question.createTail("RESTful API 설계 시 가장 중요하게 고려하는 점은 무엇인가요?", questions.get(11)));
            
            // 9-10번: 개인 질문들
            logicalQuestions.add(questions.get(2));  // DEFAULT: 강점과 약점
            logicalQuestions.add(questions.get(3));  // DEFAULT: 지원 동기
            
            // 새로 생성한 꼬리질문들 저장
            questionRepository.save(logicalQuestions.get(1));  // 자바 꼬리질문
            questionRepository.save(logicalQuestions.get(3));  // 스프링 꼬리질문  
            questionRepository.save(logicalQuestions.get(5));  // JPA 꼬리질문
            questionRepository.save(logicalQuestions.get(7));  // REST API 꼬리질문
            
            createInterviewRecordsForInterview(interview, logicalQuestions, mainUser);
        }

        // 진행 중인 면접 데이터 생성 (1개) - userId=1이 면접관이자 면접자
        Interview ongoingInterview = Interview.builder()
                .interviewer(mainUser)    // userId=1
                .interviewee(mainUser)    // userId=1 (동일 사용자)
                .usedQuestionSet(questionSet)
                .roomId("ongoing_room_" + System.currentTimeMillis())
                .build();
        interviewRepository.save(ongoingInterview);

        // 진행 중인 면접에는 논리적 순서로 7개 질문-답변 생성
        List<Question> ongoingQuestions = new ArrayList<>();
        
        // 1-2번: 스프링 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(1));  // DEFAULT: 스프링 개념
        ongoingQuestions.add(Question.createTail("실제 프로젝트에서 DI를 어떻게 활용해보셨나요?", questions.get(1)));
        
        // 3-4번: REST API 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(11));  // CUSTOM: REST API 설계
        ongoingQuestions.add(Question.createTail("RESTful API에서 상태코드는 어떻게 선택하시나요?", questions.get(11)));
        
        // 5-6번: 트랜잭션 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(12));  // CUSTOM: ACID 특성
        ongoingQuestions.add(Question.createTail("트랜잭션 격리 수준에 대해 설명해주세요.", questions.get(12)));
        
        // 7번: 개인 질문
        ongoingQuestions.add(questions.get(4));  // DEFAULT: 팀워크
        
        // 새로 생성한 꼬리질문들 저장
        questionRepository.save(ongoingQuestions.get(1));  // 스프링 꼬리질문
        questionRepository.save(ongoingQuestions.get(3));  // REST API 꼬리질문
        questionRepository.save(ongoingQuestions.get(5));  // 트랜잭션 꼬리질문
        
        createInterviewRecordsForInterview(ongoingInterview, ongoingQuestions, mainUser);

        System.out.println("InterviewRecord 더미 데이터 생성 완료!");
        System.out.println("생성된 완료 면접 개수: 3개 (각 10개 질답)");
        System.out.println("생성된 진행중 면접 개수: 1개 (7개 질답)");
    }

    /**
     * 특정 사용자를 위한 InterviewRecord 더미 데이터 생성
     */
    public void createInterviewRecordsForUser(Long userId) {
        // 로그인한 사용자 조회
        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("userId=" + userId + "인 사용자가 존재하지 않습니다."));

        // 기본 질문 세트 생성 (간단하게)
        List<Question> questions = createBasicQuestions();

        // 더미 질문 세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(loginUser)
                .title("개인 면접 연습 질문 세트")
                .description("개인 면접 연습을 위한 질문 모음입니다.")
                .versionNumber(1)
                .isPublic(false)
                .build();
        questionSet = questionSetRepository.saveAndFlush(questionSet);

        createInterviewRecordsForSpecificUser(loginUser, questionSet, questions);

        System.out.println("사용자 " + userId + "의 InterviewRecord 더미 데이터 생성 완료!");
        System.out.println("생성된 완료 면접 개수: 3개 (각 10개 질답)");
        System.out.println("생성된 진행중 면접 개수: 1개 (7개 질답)");
    }

    private List<Question> createBasicQuestions() {
        List<Question> questions = new ArrayList<>();

        // DEFAULT 타입 질문들 (10개)
        questions.add(Question.createDefault("자바의 특징에 대해 설명해주세요.", "객체지향, 플랫폼 독립성, 가비지 컬렉션 등"));
        questions.add(Question.createDefault("스프링 프레임워크의 핵심 개념은 무엇인가요?", "IoC, DI, AOP, PSA"));
        questions.add(Question.createDefault("본인의 강점과 약점을 말씀해주세요.", "상황에 맞는 개인적 답변"));
        questions.add(Question.createDefault("이 회사에 지원한 동기는 무엇인가요?", "회사 비전과 개인 목표의 일치"));
        questions.add(Question.createDefault("팀워크에서 중요하게 생각하는 것은?", "소통, 협력, 상호 존중"));
        questions.add(Question.createDefault("객체지향 프로그래밍의 4가지 특성을 설명해주세요.", "캡슐화, 상속, 다형성, 추상화"));
        questions.add(Question.createDefault("데이터베이스의 정규화가 무엇인지 설명해주세요.", "데이터 중복을 최소화하고 일관성을 유지하는 과정"));
        questions.add(Question.createDefault("HTTP와 HTTPS의 차이점은 무엇인가요?", "HTTPS는 SSL/TLS를 통한 암호화된 HTTP"));
        questions.add(Question.createDefault("본인이 가장 자신있는 기술 스택은 무엇인가요?", "개인의 경험과 전문성에 따른 답변"));
        questions.add(Question.createDefault("향후 5년간의 커리어 계획을 말씀해주세요.", "개인의 성장 목표와 비전"));

        // CUSTOM 타입 질문들
        questions.add(Question.createCustom("JPA와 Hibernate의 차이점은?"));
        questions.add(Question.createCustom("REST API 설계 원칙을 설명하세요."));
        questions.add(Question.createCustom("데이터베이스 트랜잭션의 ACID 특성은?"));

        // 기본 질문들 저장
        questionRepository.saveAll(questions);
        return questions;
    }

    private void createInterviewRecordsForSpecificUser(User user, QuestionSet questionSet, List<Question> questions) {
        // 완료된 면접 데이터 생성 (3개)
        for (int interviewNum = 1; interviewNum <= 3; interviewNum++) {
            Interview interview = Interview.builder()
                    .interviewer(user)
                    .interviewee(user)
                    .usedQuestionSet(questionSet)
                    .roomId("room_" + user.getUserId() + "_" + interviewNum + "_" + System.currentTimeMillis())
                    .build();
            interview.complete();
            interviewRepository.save(interview);

            // 10개의 논리적 질문-답변 기록 생성
            List<Question> logicalQuestions = createLogicalQuestionsForInterview(questions);
            createInterviewRecordsForInterview(interview, logicalQuestions, user);
        }

        // 진행 중인 면접 데이터 생성 (1개)
        Interview ongoingInterview = Interview.builder()
                .interviewer(user)
                .interviewee(user)
                .usedQuestionSet(questionSet)
                .roomId("ongoing_room_" + user.getUserId() + "_" + System.currentTimeMillis())
                .build();
        interviewRepository.save(ongoingInterview);

        // 7개의 논리적 질문-답변 생성
        List<Question> ongoingQuestions = createLogicalQuestionsForOngoing(questions);
        createInterviewRecordsForInterview(ongoingInterview, ongoingQuestions, user);
    }

    private List<Question> createLogicalQuestionsForInterview(List<Question> questions) {
        List<Question> logicalQuestions = new ArrayList<>();
        
        // 1-2번: 자바 질문 + 꼬리질문
        logicalQuestions.add(questions.get(0));
        logicalQuestions.add(Question.createTail("그렇다면 가비지 컬렉션의 동작 원리에 대해 더 자세히 설명해주세요.", questions.get(0)));
        
        // 3-4번: 스프링 질문 + 꼬리질문  
        logicalQuestions.add(questions.get(1));
        logicalQuestions.add(Question.createTail("실제 프로젝트에서 DI를 어떻게 활용해보셨나요?", questions.get(1)));
        
        // 5-6번: JPA 질문 + 꼬리질문
        logicalQuestions.add(questions.get(10));
        logicalQuestions.add(Question.createTail("JPA를 실제 프로젝트에서 사용하면서 어려웠던 점은 무엇인가요?", questions.get(10)));
        
        // 7-8번: REST API 질문 + 꼬리질문
        logicalQuestions.add(questions.get(11));
        logicalQuestions.add(Question.createTail("RESTful API 설계 시 가장 중요하게 고려하는 점은 무엇인가요?", questions.get(11)));
        
        // 9-10번: 개인 질문들
        logicalQuestions.add(questions.get(2));
        logicalQuestions.add(questions.get(3));
        
        // 새로 생성한 꼬리질문들 저장
        questionRepository.save(logicalQuestions.get(1));
        questionRepository.save(logicalQuestions.get(3));
        questionRepository.save(logicalQuestions.get(5));
        questionRepository.save(logicalQuestions.get(7));
        
        return logicalQuestions;
    }

    private List<Question> createLogicalQuestionsForOngoing(List<Question> questions) {
        List<Question> ongoingQuestions = new ArrayList<>();
        
        // 1-2번: 스프링 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(1));
        ongoingQuestions.add(Question.createTail("실제 프로젝트에서 DI를 어떻게 활용해보셨나요?", questions.get(1)));
        
        // 3-4번: REST API 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(11));
        ongoingQuestions.add(Question.createTail("RESTful API에서 상태코드는 어떻게 선택하시나요?", questions.get(11)));
        
        // 5-6번: 트랜잭션 질문 + 꼬리질문
        ongoingQuestions.add(questions.get(12));
        ongoingQuestions.add(Question.createTail("트랜잭션 격리 수준에 대해 설명해주세요.", questions.get(12)));
        
        // 7번: 개인 질문
        ongoingQuestions.add(questions.get(4));
        
        // 새로 생성한 꼬리질문들 저장
        questionRepository.save(ongoingQuestions.get(1));
        questionRepository.save(ongoingQuestions.get(3));
        questionRepository.save(ongoingQuestions.get(5));
        
        return ongoingQuestions;
    }

    private void createInterviewRecordsForInterview(Interview interview, List<Question> questionsForInterview, User answeringUser) {
        List<String> sampleAnswers = List.of(
                "네, 저는 이 부분에 대해서 다음과 같이 생각합니다...",
                "제 경험을 바탕으로 말씀드리면, 이런 상황에서는...",
                "좋은 질문이네요. 이 문제에 대해서는...",
                "개인적으로는 이런 접근 방식이 효과적이라고 생각합니다...",
                "과거 프로젝트에서 비슷한 경험이 있었는데...",
                "기술적인 관점에서 보면 이 방법이 더 적합할 것 같습니다...",
                "팀워크의 중요성에 대해서는 저는 이렇게 생각합니다...",
                "성장과 학습에 대한 제 철학은...",
                "이 회사의 비전과 제 목표가 잘 맞는다고 생각하는 이유는...",
                "스트레스 관리를 위해 제가 사용하는 방법은..."
        );

        for (int i = 0; i < questionsForInterview.size(); i++) {
            Question question = questionsForInterview.get(i);
            
            // Answer 생성
            Answer answer = Answer.create(
                    sampleAnswers.get(i % sampleAnswers.size()),
                    answeringUser
            );
            answerRepository.save(answer);

            // InterviewRecord 생성
            InterviewRecord record = InterviewRecord.builder()
                    .interview(interview)
                    .question(question)
                    .answer(answer)
                    .orderNum(i + 1)
                    .build();
            interviewRecordRepository.save(record);
        }
    }

    private void createBasicTags() {
        // 기본 태그들을 먼저 생성하고 저장
        createOrGetTag("Java");
        createOrGetTag("Spring");
        createOrGetTag("Backend");
    }
    
    private void linkQuestionSetToExistingTags(QuestionSet questionSet) {
        // 이미 생성된 태그들을 조회해서 연결
        Tag javaTag = tagRepository.findByTag("Java")
                .orElseThrow(() -> new RuntimeException("Java 태그가 존재하지 않습니다."));
        Tag springTag = tagRepository.findByTag("Spring")
                .orElseThrow(() -> new RuntimeException("Spring 태그가 존재하지 않습니다."));
        Tag backendTag = tagRepository.findByTag("Backend")
                .orElseThrow(() -> new RuntimeException("Backend 태그가 존재하지 않습니다."));
        
        // 질문 세트와 태그 연결
        linkQuestionSetToTag(questionSet, javaTag);
        linkQuestionSetToTag(questionSet, springTag);
        linkQuestionSetToTag(questionSet, backendTag);
    }
    
    private Tag createOrGetTag(String tagName) {
        return tagRepository.findByTag(tagName)
                .orElseGet(() -> {
                    Tag tag = Tag.builder().name(tagName).build();
                    return tagRepository.saveAndFlush(tag);
                });
    }
    
    private void linkQuestionSetToTag(QuestionSet questionSet, Tag tag) {
        QuestionSetTag questionSetTag = QuestionSetTag.builder()
                .questionSet(questionSet)
                .tag(tag)
                .build();
        questionSetTagRepository.save(questionSetTag);
    }

    // SQL 파일 실행 메서드들
    private void executeSqlFile(String fileName) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // 주석 및 빈 줄 무시
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line).append(" ");
                
                // 세미콜론으로 끝나는 경우 SQL 실행
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    if (!sql.isEmpty()) {
                        executeStatement(sql);
                    }
                    sqlBuilder.setLength(0);
                }
            }
            
            // 마지막 SQL문이 세미콜론으로 끝나지 않는 경우
            String remainingSql = sqlBuilder.toString().trim();
            if (!remainingSql.isEmpty()) {
                executeStatement(remainingSql);
            }
        }
    }
    
    private void executeStatement(String sql) {
        try {
            // INSERT, UPDATE, DELETE 문 실행
            if (sql.toUpperCase().startsWith("INSERT") || 
                sql.toUpperCase().startsWith("UPDATE") || 
                sql.toUpperCase().startsWith("DELETE")) {
                jdbcTemplate.update(sql);
            } else {
                // 기타 SQL문 (CREATE, ALTER 등)
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            System.err.println("SQL 실행 실패: " + sql);
            System.err.println("에러: " + e.getMessage());
            // 개별 SQL 실행 실패는 로그만 남기고 계속 진행
        }
    }

    private void createAdditionalUsersAndQuestionSets() {
        // 1. 30명의 추가 유저 생성 (독립적)
        List<User> additionalUsers = createAdditionalUsers();
        
        // 2. 추가 태그들 생성 (독립적)
        List<Tag> additionalTags = createAdditionalTags();
        
        // 3. 분야별 질문들 생성 (독립적)
        Map<String, List<Question>> categorizedQuestions = createCategorizedQuestions();
        
        // 4. 30개의 추가 질문 세트 생성 (User 필요)
        List<QuestionSet> questionSets = createAdditionalQuestionSets(additionalUsers);
        
        // 5. QuestionSet에 Tag 연결 (QuestionSet + Tag 필요)
        linkTagsToQuestionSets(questionSets, additionalTags);
        
        // 6. QuestionSet에 Question 연결 (QuestionSet + Question 필요)
        linkQuestionsToQuestionSetsLogically(questionSets, categorizedQuestions);
        
        System.out.println("추가 데이터 생성 완료 - 유저 30명, 질문세트 30개, 태그 8개, 기본질문 추가");
    }
    
    private List<User> createAdditionalUsers() {
        String[] userNames = {
            "김개발자", "이프론트", "박백엔드", "최데브옵스", "정풀스택",
            "한모바일", "강웹개발", "조게임개발", "윤데이터", "신머신러닝",
            "문AI개발", "송클라우드", "배보안전문", "임QA엔지니어", "UI/UX디자이너",
            "프로덕트매니저", "테크리드", "시니어개발자", "주니어개발자", "인턴개발자",
            "아키텍트", "스크럼마스터", "데이터엔지니어", "플랫폼엔지니어", "인프라엔지니어",
            "네트워크엔지니어", "시스템관리자", "DBA전문가", "성능튜닝전문", "코드리뷰어"
        };
        
        List<User> users = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < userNames.length; i++) {
            User user = User.builder()
                    .sub("kakao_dev" + String.format("%03d", i + 1) + "_" + timestamp)
                    .nickname(userNames[i])
                    .build();
            users.add(userRepository.save(user));
        }
        
        return users;
    }
    
    private List<Tag> createAdditionalTags() {
        List<String> additionalTagNames = List.of(
            "백엔드", "프론트엔드", "데이터베이스", "네트워크", 
            "운영체제", "알고리즘", "자료구조", "JavaScript"
        );
        
        List<Tag> additionalTags = new ArrayList<>();
        for (String tagName : additionalTagNames) {
            Tag tag = createOrGetTag(tagName);
            additionalTags.add(tag);
        }
        return additionalTags;
    }
    
    private Map<String, List<Question>> createCategorizedQuestions() {
        Map<String, List<Question>> categorizedQuestions = new HashMap<>();
        
        // Java 관련 질문 10개
        List<Question> javaQuestions = createQuestionsForCategory("Java", new String[][]{
            {"Java의 특징에 대해 설명해주세요.", "객체지향, 플랫폼 독립성, 가비지 컬렉션"},
            {"JVM의 동작 원리는?", "바이트코드 실행, 메모리 관리, 최적화"},
            {"String과 StringBuffer의 차이는?", "불변성, 성능, 메모리 사용"},
            {"추상클래스와 인터페이스의 차이?", "구현 여부, 다중 상속, 용도"},
            {"Exception과 Error의 차이는?", "복구 가능성, 처리 방법, 발생 시점"},
            {"컬렉션 프레임워크의 종류는?", "List, Set, Map, Queue의 특징"},
            {"람다 표현식이란?", "함수형 프로그래밍, 익명 함수, 코드 간소화"},
            {"Stream API의 장점은?", "선언적 프로그래밍, 병렬 처리, 가독성"},
            {"멀티스레딩 동기화 방법은?", "synchronized, Lock, Atomic 클래스"},
            {"가비지 컬렉션의 종류는?", "Serial, Parallel, G1, ZGC"}
        });
        categorizedQuestions.put("Java", javaQuestions);

        // Spring 관련 질문 10개
        List<Question> springQuestions = createQuestionsForCategory("Spring", new String[][]{
            {"Spring Framework의 핵심 개념은?", "IoC, DI, AOP, PSA"},
            {"Spring Boot의 장점은?", "자동 설정, 내장 서버, 스타터 의존성"},
            {"Bean의 생명주기는?", "생성, 초기화, 사용, 소멸 단계"},
            {"AOP란 무엇인가요?", "관점 지향 프로그래밍, 횡단 관심사 분리"},
            {"Spring MVC 패턴은?", "Model-View-Controller 아키텍처"},
            {"Spring Security의 역할은?", "인증, 인가, 보안 설정"},
            {"JPA와 Hibernate의 관계는?", "표준 명세와 구현체의 관계"},
            {"트랜잭션 관리 방법은?", "@Transactional, 선언적 트랜잭션"},
            {"Spring Profile이란?", "환경별 설정 분리, 조건부 Bean 등록"},
            {"RESTful 웹 서비스 구현 방법은?", "@RestController, HTTP 메서드 매핑"}
        });
        categorizedQuestions.put("Spring", springQuestions);

        // React 관련 질문 10개
        List<Question> reactQuestions = createQuestionsForCategory("React", new String[][]{
            {"React의 장점은 무엇인가요?", "가상 DOM, 컴포넌트 기반, 재사용성"},
            {"JSX란 무엇인가요?", "JavaScript XML, 컴포넌트 작성 문법"},
            {"State와 Props의 차이는?", "내부 상태 vs 외부에서 전달받는 데이터"},
            {"생명주기 메서드의 종류는?", "Mount, Update, Unmount 단계"},
            {"Hook이란 무엇인가요?", "함수형 컴포넌트에서 상태 관리"},
            {"useEffect의 용도는?", "사이드 이펙트 처리, 생명주기 대체"},
            {"Context API란?", "전역 상태 관리, Props drilling 해결"},
            {"Redux와 useState의 차이?", "전역 vs 지역 상태 관리"},
            {"Virtual DOM의 동작 원리는?", "메모리 상 DOM 표현, 효율적 업데이트"},
            {"React Router의 역할은?", "SPA에서 라우팅, 페이지 네비게이션"}
        });
        categorizedQuestions.put("React", reactQuestions);

        // 데이터베이스 관련 질문 10개
        List<Question> dbQuestions = createQuestionsForCategory("Database", new String[][]{
            {"데이터베이스 정규화란?", "중복 제거, 일관성 유지, 이상현상 방지"},
            {"인덱스의 장단점은?", "조회 성능 향상 vs 저장공간 증가"},
            {"트랜잭션의 ACID 속성은?", "원자성, 일관성, 고립성, 지속성"},
            {"JOIN의 종류는?", "INNER, LEFT, RIGHT, FULL OUTER JOIN"},
            {"NoSQL과 RDBMS의 차이?", "스키마 유연성, 확장성, 일관성"},
            {"데이터베이스 락의 종류는?", "공유락, 배타락, 데드락"},
            {"파티셔닝과 샤딩의 차이?", "수직 분할 vs 수평 분할"},
            {"쿼리 최적화 방법은?", "인덱스 활용, 실행계획 분석"},
            {"백업과 복구 전략은?", "풀백업, 증분백업, 로그백업"},
            {"데이터 무결성이란?", "개체, 참조, 도메인 무결성"}
        });
        categorizedQuestions.put("Database", dbQuestions);

        // 네트워크 관련 질문 10개
        List<Question> networkQuestions = createQuestionsForCategory("Network", new String[][]{
            {"TCP와 UDP의 차이점은?", "신뢰성 vs 속도, 연결지향 vs 비연결"},
            {"HTTP와 HTTPS의 차이?", "평문 전송 vs 암호화 전송"},
            {"OSI 7계층 모델은?", "물리, 데이터링크, 네트워크, 전송, 세션, 표현, 응용"},
            {"DNS의 동작 원리는?", "도메인 이름을 IP 주소로 변환"},
            {"CDN이란 무엇인가요?", "콘텐츠 배포 네트워크, 지리적 분산"},
            {"로드 밸런싱이란?", "트래픽 분산, 가용성 향상"},
            {"방화벽의 역할은?", "네트워크 보안, 트래픽 제어"},
            {"VPN의 원리는?", "가상 사설망, 보안 터널링"},
            {"HTTP 상태 코드의 의미는?", "2xx 성공, 4xx 클라이언트 오류, 5xx 서버 오류"},
            {"REST API 설계 원칙은?", "무상태, 캐시 가능, 계층화 시스템"}
        });
        categorizedQuestions.put("Network", networkQuestions);

        // 운영체제 관련 질문 10개
        List<Question> osQuestions = createQuestionsForCategory("OS", new String[][]{
            {"프로세스와 스레드의 차이?", "독립적 메모리 vs 공유 메모리"},
            {"CPU 스케줄링 알고리즘은?", "FCFS, SJF, Round Robin, Priority"},
            {"데드락이란?", "교착상태, 상호배제, 점유대기, 비선점, 순환대기"},
            {"가상 메모리란?", "물리 메모리 한계 극복, 페이징"},
            {"시스템 콜이란?", "커널 모드 진입, OS 서비스 요청"},
            {"뮤텍스와 세마포어의 차이?", "이진 vs 카운팅 동기화"},
            {"페이지 교체 알고리즘은?", "FIFO, LRU, LFU, Optimal"},
            {"인터럽트의 종류는?", "하드웨어, 소프트웨어 인터럽트"},
            {"캐시 메모리의 역할은?", "CPU-메모리 속도 차이 극복"},
            {"파일 시스템이란?", "파일 저장 및 관리 방법"}
        });
        categorizedQuestions.put("OS", osQuestions);

        // 알고리즘 관련 질문 10개
        List<Question> algorithmQuestions = createQuestionsForCategory("Algorithm", new String[][]{
            {"시간복잡도 O(n)이란?", "입력 크기에 비례하는 시간"},
            {"정렬 알고리즘의 종류는?", "버블, 선택, 삽입, 퀵, 머지 정렬"},
            {"이진 탐색의 조건은?", "정렬된 배열, O(log n) 시간복잡도"},
            {"그래프 탐색 방법은?", "DFS, BFS의 차이점과 활용"},
            {"동적 계획법이란?", "중복 부분 문제, 메모이제이션"},
            {"탐욕 알고리즘이란?", "지역 최적해 선택, 전역 최적해 근사"},
            {"해시 테이블의 특징은?", "O(1) 평균 접근 시간, 충돌 해결"},
            {"최단 경로 알고리즘은?", "다익스트라, 벨만-포드, 플로이드"},
            {"문자열 매칭 알고리즘은?", "KMP, 라빈-카프, 보이어-무어"},
            {"분할 정복 알고리즘이란?", "문제 분할, 재귀적 해결, 결합"}
        });
        categorizedQuestions.put("Algorithm", algorithmQuestions);

        // 자료구조 관련 질문 10개
        List<Question> dataStructureQuestions = createQuestionsForCategory("DataStructure", new String[][]{
            {"스택과 큐의 차이점은?", "LIFO vs FIFO 구조"},
            {"연결리스트의 장단점은?", "동적 크기 vs 메모리 오버헤드"},
            {"이진트리의 특징은?", "각 노드가 최대 2개의 자식"},
            {"해시맵의 동작 원리는?", "해시 함수, 버킷, 충돌 처리"},
            {"힙의 특징은?", "완전 이진트리, 우선순위 큐 구현"},
            {"B-트리란?", "다진 탐색 트리, 데이터베이스 인덱스"},
            {"그래프의 표현 방법은?", "인접 리스트 vs 인접 행렬"},
            {"트라이 자료구조란?", "문자열 검색, 접두사 트리"},
            {"세그먼트 트리의 용도는?", "구간 쿼리, 범위 업데이트"},
            {"유니온 파인드란?", "분리 집합, 경로 압축 최적화"}
        });
        categorizedQuestions.put("DataStructure", dataStructureQuestions);

        // JavaScript 관련 질문 10개
        List<Question> jsQuestions = createQuestionsForCategory("JavaScript", new String[][]{
            {"JavaScript의 특징은?", "동적 타입, 프로토타입 기반, 비동기"},
            {"var, let, const의 차이?", "스코프, 호이스팅, 재할당"},
            {"클로저란 무엇인가요?", "함수와 렉시컬 환경의 조합"},
            {"프로토타입 체인이란?", "객체 상속 메커니즘, __proto__"},
            {"비동기 처리 방법은?", "콜백, Promise, async/await"},
            {"이벤트 루프란?", "싱글 스레드, 콜 스택, 태스크 큐"},
            {"ES6의 주요 기능은?", "화살표 함수, 클래스, 모듈, 구조분해"},
            {"호이스팅이란?", "변수와 함수 선언의 끌어올림"},
            {"스코프의 종류는?", "전역, 함수, 블록 스코프"},
            {"DOM 조작 방법은?", "getElementById, querySelector, 이벤트 리스너"}
        });
        categorizedQuestions.put("JavaScript", jsQuestions);

        return categorizedQuestions;
    }
    
    private List<Question> createQuestionsForCategory(String category, String[][] questionData) {
        List<Question> questions = new ArrayList<>();
        for (String[] data : questionData) {
            Question question = Question.createDefault(data[0], data[1]);
            questions.add(questionRepository.saveAndFlush(question));
        }
        return questions;
    }
    
    private List<QuestionSet> createAdditionalQuestionSets(List<User> users) {
        // 질문 세트 데이터 (제목, 설명, 공개여부, 카테고리)
        String[][] questionSetData = {
            {"Java 백엔드 면접 질문", "Spring Boot와 JPA 중심의 백엔드 개발 면접 질문 모음", "true", "Java"},
            {"React 프론트엔드 면접", "React, TypeScript, Next.js 관련 프론트엔드 면접 준비", "true", "React"},
            {"알고리즘 코딩테스트 대비", "자주 출제되는 알고리즘 문제와 해결 방법", "true", "Algorithm"},
            {"Spring Framework 면접", "Spring Boot, DI, AOP 관련 심화 질문", "true", "Spring"},
            {"데이터베이스 심화 면접", "MySQL, PostgreSQL, 인덱싱, 쿼리 최적화", "true", "Database"},
            {"JavaScript 심화 면접", "ES6+, 비동기, 클로저 관련 질문", "true", "JavaScript"},
            {"네트워크 엔지니어 면접", "TCP/IP, 라우팅, 스위칭 네트워크 기초", "true", "Network"},
            {"운영체제 기초 면접", "프로세스, 스레드, 메모리 관리", "true", "OS"},
            {"자료구조 기본 면접", "스택, 큐, 트리, 그래프 관련 질문", "true", "DataStructure"},
            {"Java 고급 면접", "JVM, GC, 멀티스레딩 심화 질문", "false", "Java"},
            {"React 고급 면접", "Context, Hook, 성능 최적화", "true", "React"},
            {"알고리즘 고급 면접", "동적계획법, 그래프 알고리즘", "true", "Algorithm"},
            {"Spring 심화 면접", "Security, JPA, 트랜잭션 관리", "true", "Spring"},
            {"DB 최적화 면접", "인덱스, 쿼리 튜닝, 샤딩", "true", "Database"},
            {"JS 비동기 처리 면접", "Promise, async/await, 이벤트 루프", "true", "JavaScript"},
            {"네트워크 보안 면접", "HTTPS, VPN, 방화벽 설정", "true", "Network"},
            {"OS 고급 면접", "가상메모리, 데드락, 스케줄링", "true", "OS"},
            {"고급 자료구조 면접", "B-트리, 세그먼트 트리, Union-Find", "true", "DataStructure"},
            {"풀스택 Java 면접", "Java + React 통합 개발", "true", "Java"},
            {"모던 React 면접", "Hooks, Suspense, 최신 기능", "false", "React"},
            {"코딩테스트 실전 면접", "실제 기업 출제 문제 유형", "true", "Algorithm"},
            {"Spring Boot 실무 면접", "실제 프로젝트 경험 기반 질문", "true", "Spring"},
            {"대용량 DB 설계 면접", "분산 데이터베이스, NoSQL", "true", "Database"},
            {"Node.js vs JavaScript 면접", "서버사이드 JavaScript", "true", "JavaScript"},
            {"클라우드 네트워킹 면접", "AWS, 로드밸런싱, CDN", "true", "Network"},
            {"임베디드 OS 면접", "리눅스 커널, 디바이스 드라이버", "false", "OS"},
            {"게임 자료구조 면접", "공간분할, 충돌감지 알고리즘", "true", "DataStructure"},
            {"시니어 Java 면접", "아키텍처 설계, 성능 튜닝", "true", "Java"},
            {"테크리드 면접", "기술 의사결정, 팀 관리", "false", "Algorithm"},
            {"오픈소스 기여자 면접", "GitHub, 협업, 커뮤니티 활동", "true", "JavaScript"}
        };
        
        List<QuestionSet> questionSets = new ArrayList<>();
        for (int i = 0; i < questionSetData.length; i++) {
            User owner = users.get(i % users.size()); // 유저를 순환하며 할당
            
            QuestionSet questionSet = QuestionSet.builder()
                    .ownerUserId(owner)
                    .title(questionSetData[i][0])
                    .description(questionSetData[i][1])
                    .versionNumber(1)
                    .isPublic(Boolean.parseBoolean(questionSetData[i][2]))
                    .build();
            
            QuestionSet savedQuestionSet = questionSetRepository.saveAndFlush(questionSet);
            questionSets.add(savedQuestionSet);
        }
        
        return questionSets;
    }
    
    private void linkQuestionsToQuestionSetsLogically(List<QuestionSet> questionSets, Map<String, List<Question>> categorizedQuestions) {
        // 질문 세트 데이터 (카테고리 매핑용)
        String[] categories = {
            "Java", "React", "Algorithm", "Spring", "Database", "JavaScript", "Network", "OS", "DataStructure",
            "Java", "React", "Algorithm", "Spring", "Database", "JavaScript", "Network", "OS", "DataStructure",
            "Java", "React", "Algorithm", "Spring", "Database", "JavaScript", "Network", "OS", "DataStructure",
            "Java", "React", "Algorithm", "Spring", "Database", "JavaScript", "Network", "OS", "DataStructure",
            "Java", "React", "Algorithm", "Spring", "Database", "JavaScript", "Network", "OS", "DataStructure"
        };
        
        for (int i = 0; i < questionSets.size() && i < categories.length; i++) {
            QuestionSet questionSet = questionSets.get(i);
            String category = categories[i];
            
            List<Question> questionsForCategory = categorizedQuestions.get(category);
            if (questionsForCategory != null && !questionsForCategory.isEmpty()) {
                // 해당 카테고리의 모든 질문을 질문 세트에 추가
                for (int order = 0; order < questionsForCategory.size(); order++) {
                    QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.builder()
                            .questionSet(questionSet)
                            .question(questionsForCategory.get(order))
                            .displayOrder(order + 1)
                            .build();
                    questionSetQuestionMapRepository.save(questionMap);
                }
            }
        }
    }
    
    private void linkTagsToQuestionSets(List<QuestionSet> questionSets, List<Tag> tags) {
        for (QuestionSet questionSet : questionSets) {
            // 각 질문 세트에 랜덤하게 2-3개의 태그 연결
            linkRandomTagsToQuestionSet(questionSet, tags);
        }
    }
    
    
    private void linkRandomTagsToQuestionSet(QuestionSet questionSet, List<Tag> availableTags) {
        // 2-3개의 랜덤 태그를 선택해서 연결
        int tagCount = 2 + (int)(Math.random() * 2); // 2 또는 3개
        List<Tag> selectedTags = new ArrayList<>();
        
        for (int i = 0; i < tagCount; i++) {
            Tag randomTag = availableTags.get((int)(Math.random() * availableTags.size()));
            if (!selectedTags.contains(randomTag)) {
                selectedTags.add(randomTag);
            }
        }
        
        for (Tag tag : selectedTags) {
            linkQuestionSetToTag(questionSet, tag);
        }
    }
}
