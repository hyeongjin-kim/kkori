package com.kkori.dummy;

import com.kkori.entity.Question;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.QuestionType;
import com.kkori.entity.User;
import com.kkori.repository.QuestionRepository;
import com.kkori.repository.QuestionSetQuestionMapRepository;
import com.kkori.repository.QuestionSetRepository;
import com.kkori.repository.UserRepository;
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

    public void createDummyData() {
        // 1. 더미 유저 생성
        User dummyUser = User.builder()
                .sub("dummy_user_" + System.currentTimeMillis())
                .nickname("테스트유저")
                .build();
        userRepository.save(dummyUser);

        // 2. 더미 질문들 생성 (10개)
        List<Question> questions = new ArrayList<>();

        // CUSTOM 타입 질문들
        questions.add(Question.of("자바의 특징에 대해 설명해주세요.", "객체지향, 플랫폼 독립성, 메모리 관리 등", QuestionType.CUSTOM));
        questions.add(Question.of("스프링 프레임워크의 핵심 개념은 무엇인가요?", "IoC, DI, AOP", QuestionType.CUSTOM));
        questions.add(Question.of("JPA와 Hibernate의 차이점은?", "JPA는 명세, Hibernate는 구현체", QuestionType.CUSTOM));
        questions.add(Question.of("REST API 설계 원칙을 설명하세요.", "HTTP 메서드 활용, 상태없음, 캐시 가능성 등", QuestionType.CUSTOM));
        questions.add(Question.of("데이터베이스 트랜잭션의 ACID 특성은?", "Atomicity, Consistency, Isolation, Durability", QuestionType.CUSTOM));

        // DEFAULT 타입 질문들
        questions.add(Question.of("본인의 강점과 약점을 말씀해주세요.", "상황에 맞는 개인적 답변", QuestionType.DEFAULT));
        questions.add(Question.of("이 회사에 지원한 동기는 무엇인가요?", "회사 비전과 개인 목표의 일치", QuestionType.DEFAULT));
        questions.add(Question.of("5년 후 본인의 모습을 그려보세요.", "커리어 비전과 성장 계획", QuestionType.DEFAULT));
        questions.add(Question.of("팀워크에서 중요하게 생각하는 것은?", "소통, 협력, 상호 존중", QuestionType.DEFAULT));
        questions.add(Question.of("스트레스를 받을 때 어떻게 해결하시나요?", "개인만의 스트레스 해소 방법", QuestionType.DEFAULT));

        // 질문들 저장
        questionRepository.saveAll(questions);

        // 3. 질문 세트 생성
        QuestionSet questionSet = QuestionSet.builder()
                .ownerUserId(dummyUser)
                .title("백엔드 개발자 면접 질문 세트")
                .description("Java/Spring 백엔드 개발자를 위한 기본 면접 질문 모음입니다.")
                .versionNumber(1)
                .isShared(true)
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

        System.out.println("더미 데이터 생성 완료!");
        System.out.println("생성된 유저 ID: " + dummyUser.getUserId());
        System.out.println("생성된 질문 세트 ID: " + questionSet.getId());
        System.out.println("생성된 질문 개수: " + questions.size());
    }
}
