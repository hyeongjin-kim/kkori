package com.kkori.dummy;

import com.kkori.entity.*;
import com.kkori.component.interview.RoomStatus;
import com.kkori.repository.*;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void createDummyData() {
        // 1. 더미 유저 생성
        User dummyUser = User.builder()
                .sub("dummy_user_" + System.currentTimeMillis())
                .nickname("테스트유저")
                .build();
        userRepository.save(dummyUser);

        // 2. 더미 질문들 생성 (기본 질문 + 꼬리 질문)
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

        // 3. 질문 세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(dummyUser)
                .title("백엔드 개발자 면접 질문 세트")
                .description("Java/Spring 백엔드 개발자를 위한 기본 면접 질문 모음입니다.")
                .versionNumber(1)
                .isPublic(true)
                .build();
        questionSetRepository.save(questionSet);

        // 4. 질문 세트와 질문들 매핑
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

        // 5. InterviewRecord 더미 데이터 생성
        createInterviewRecords(dummyUser, questionSet, questions);

        System.out.println("더미 데이터 생성 완료!");
        System.out.println("생성된 유저 ID: " + dummyUser.getUserId());
        System.out.println("생성된 질문 세트 ID: " + questionSet.getId());
        System.out.println("생성된 질문 개수: " + questions.size());
    }

    private void createInterviewRecords(User dummyUser, QuestionSet questionSet, List<Question> questions) {
        // userId=1인 기존 사용자 조회 (이미 존재한다고 가정)
        User mainUser = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("userId=1인 사용자가 존재하지 않습니다."));

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
        questionSetRepository.save(questionSet);

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
}
