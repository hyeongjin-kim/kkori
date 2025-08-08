package com.kkori.repository;

import com.kkori.entity.QuestionSetQuestionMap;
import com.kkori.entity.QuestionSet;
import com.kkori.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionSetQuestionMapRepository extends JpaRepository<QuestionSetQuestionMap, Long> {

    // 특정 QuestionSet에 속한 모든 매핑을 displayOrder 순으로 조회
    List<QuestionSetQuestionMap> findByQuestionSetOrderByDisplayOrder(QuestionSet questionSet);

    // 특정 QuestionSet ID로 매핑을 displayOrder 순으로 조회
    @Query("SELECT qsqm FROM QuestionSetQuestionMap qsqm WHERE qsqm.questionSet.id = :questionSetId ORDER BY qsqm.displayOrder")
    List<QuestionSetQuestionMap> findByQuestionSetIdOrderByDisplayOrder(@Param("questionSetId") Long questionSetId);

    // 특정 Question에 대한 모든 매핑 조회
    List<QuestionSetQuestionMap> findByQuestion(Question question);

    // 특정 QuestionSet과 Question의 매핑 존재 여부 확인
    boolean existsByQuestionSetAndQuestion(QuestionSet questionSet, Question question);

    // 특정 QuestionSet과 Question의 매핑 조회
    Optional<QuestionSetQuestionMap> findByQuestionSetAndQuestion(QuestionSet questionSet, Question question);

    // 특정 QuestionSet에서 최대 displayOrder 값 조회
    @Query("SELECT MAX(qsqm.displayOrder) FROM QuestionSetQuestionMap qsqm WHERE qsqm.questionSet = :questionSet")
    Optional<Integer> findMaxDisplayOrderByQuestionSet(@Param("questionSet") QuestionSet questionSet);

    // 특정 QuestionSet의 매핑 개수 조회
    long countByQuestionSet(QuestionSet questionSet);

    // 특정 QuestionSet에 속한 매핑들 삭제
    void deleteByQuestionSet(QuestionSet questionSet);

    // 특정 Question에 속한 매핑들 삭제
    void deleteByQuestion(Question question);
}