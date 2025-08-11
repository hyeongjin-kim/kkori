package com.kkori.repository;

import com.kkori.entity.QuestionSetTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionSetTagRepository extends JpaRepository<QuestionSetTag, Long> {

    /**
     * 질문 세트의 모든 태그 조회
     */
    @Query("SELECT qst FROM QuestionSetTag qst " +
           "JOIN FETCH qst.tag " +
           "WHERE qst.questionSet.id = :questionSetId")
    List<QuestionSetTag> findByQuestionSetIdWithTag(@Param("questionSetId") Long questionSetId);

    /**
     * 질문 세트의 태그 삭제 - 배치 삭제로 최적화
     */
    @Modifying
    @Query("DELETE FROM QuestionSetTag qst WHERE qst.questionSet.id = :questionSetId")
    void deleteByQuestionSetId(@Param("questionSetId") Long questionSetId);
}