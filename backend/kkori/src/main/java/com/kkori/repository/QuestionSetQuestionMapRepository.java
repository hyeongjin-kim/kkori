package com.kkori.repository;

import com.kkori.entity.QuestionSetQuestionMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionSetQuestionMapRepository extends JpaRepository<QuestionSetQuestionMap, Long> {

    @Query("SELECT qsqm FROM QuestionSetQuestionMap qsqm " +
           "JOIN FETCH qsqm.question q " +
           "JOIN FETCH qsqm.answer a " +
           "JOIN FETCH a.createdBy " +
           "WHERE qsqm.questionSet.id = :questionSetId " +
           "ORDER BY qsqm.displayOrder ASC")
    List<QuestionSetQuestionMap> findByQuestionSetIdWithDetails(@Param("questionSetId") Long questionSetId);

    @Query("SELECT qsqm FROM QuestionSetQuestionMap qsqm " +
           "JOIN FETCH qsqm.question q " +
           "JOIN FETCH qsqm.answer a " +
           "JOIN FETCH a.createdBy " +
           "WHERE qsqm.id = :mapId")
    Optional<QuestionSetQuestionMap> findByIdWithDetails(@Param("mapId") Long mapId);

    @Query("SELECT qsqm FROM QuestionSetQuestionMap qsqm " +
           "WHERE qsqm.questionSet.id = :questionSetId " +
           "ORDER BY qsqm.displayOrder ASC")
    List<QuestionSetQuestionMap> findByQuestionSetId(@Param("questionSetId") Long questionSetId);

    @Query("SELECT COUNT(qsqm) FROM QuestionSetQuestionMap qsqm " +
           "WHERE qsqm.questionSet.id = :questionSetId")
    long countByQuestionSetId(@Param("questionSetId") Long questionSetId);

    @Query("SELECT MAX(qsqm.displayOrder) FROM QuestionSetQuestionMap qsqm " +
           "WHERE qsqm.questionSet.id = :questionSetId")
    Optional<Integer> findMaxDisplayOrderByQuestionSetId(@Param("questionSetId") Long questionSetId);

    @Query("SELECT qsqm FROM QuestionSetQuestionMap qsqm " +
           "WHERE qsqm.questionSet.id = :questionSetId " +
           "ORDER BY qsqm.displayOrder ASC")
    List<QuestionSetQuestionMap> findByQuestionSetIdOrderByDisplayOrder(@Param("questionSetId") Long questionSetId);
}