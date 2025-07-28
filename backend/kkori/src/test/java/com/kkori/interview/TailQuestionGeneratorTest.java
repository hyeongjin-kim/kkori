package com.kkori.interview;

import static org.assertj.core.api.Assertions.assertThat;

import com.kkori.component.TailQuestionGenerator;
import com.kkori.dto.QuestionForm;
import com.kkori.dto.QuestionType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("TailQuestionGenerator 로직 테스트")
public class TailQuestionGeneratorTest {
    /**
     * 뜬금없는 질답은 사람이 판단해야하기 때문에 주석으로 변경했습니다.
     */

    @Autowired
    private TailQuestionGenerator tailQuestionGenerator;
    Map<QuestionForm, String> questionAnswer = new LinkedHashMap<>();

    @BeforeEach
    void setUp() {
        QuestionForm q1 = new QuestionForm(QuestionType.DEFAULT, 1, "추상 클래스와 인터페이스에 대해 설명해주세요");
        QuestionForm q2 = new QuestionForm(QuestionType.CUSTOM, 2, "가비지 컬렉션에 대해 설명해주세요", QuestionType.DEFAULT, 1);
        QuestionForm q3 = new QuestionForm(QuestionType.TAIL, 3, "GC의 청소 방식은 어떻게 되나요?", QuestionType.CUSTOM, 2);
//        QuestionForm q3 = new QuestionForm(QuestionType.TAIL, 3, "오늘 싸피 간식 뭐야?", QuestionType.CUSTOM, 2);

        questionAnswer.put(q1,
                "추상 클래스틑 공통된 상태(필드)와 기본 동작(메서드 구현)을 가질 수 있는 클래스이고, 인터페이스는 기능 명세만 정의하고 구현은 전혀 하지 않는 구조입니다. 자바 8 이후 인터페이스도 default 메서드로 일부 구현이 가능해졌지만, 여전히 다중 상속이 필요한 경우나 유연한 설계엔 인터페이스가 더 적합합니다.");
        questionAnswer.put(q2,
                "가비지 컬렉션은 더 이상 사용되지 않는 객체를 JVM이 자동으로 메모리에서 해제하는 기능입니다. 자바는 명시적인 메모리 해제가 없기 때문에, GC가 힙 영역에서 참조되지 않는 객체를 찾아 제거해 메모리 누수를 방지합니다.");
        questionAnswer.put(q3, "reference count는 각 객체별로 참조된 횟수를 말합니다. 이 카운트가 0이 되면 GC의 대상이 됩니다.\n"
                + "하지만 이런 경우 순환참조가 발생합니다. 따라서 Mark and Sweep 방식으로 GC를 수행합니다.");
//        questionAnswer.put(q3, "나 오늘 꿀고구마 말랭이 먹었다. 꿀고구마 말랭이의 효능은 100% 국산 꿀고구마라서 우리아빠 술안주 우리아이 건강간식이야.");
    }


    @Test
    @DisplayName("질문과 답변 세트를 기반으로 꼬리질문 2개를 생성해야 한다")
    void should_generate_two_tail_questions() {
        // given & when
        List<String> tailQuestions = tailQuestionGenerator.generateTailQuestions(questionAnswer);

        // then
        System.out.println(tailQuestions.get(0) + tailQuestions.get(1));
        assertThat(tailQuestions).hasSize(2);
        assertThat(tailQuestions.get(0)).isNotEmpty();
        assertThat(tailQuestions.get(1)).isNotEmpty();
    }


}
