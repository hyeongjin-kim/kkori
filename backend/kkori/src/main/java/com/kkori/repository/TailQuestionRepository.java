package com.kkori.repository;

import com.kkori.entity.TailQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TailQuestionRepository extends JpaRepository<TailQuestion, Long> {

    @Query("SELECT tq FROM TailQuestion tq " +
           "JOIN FETCH tq.question q " +
           "JOIN FETCH tq.createdBy cb " +
           "WHERE tq.question.id = :questionId " +
           "ORDER BY tq.displayOrder ASC")
    List<TailQuestion> findByQuestionIdWithDetails(@Param("questionId") Long questionId);

    @Query("SELECT tq FROM TailQuestion tq " +
           "JOIN FETCH tq.question q " +
           "JOIN FETCH tq.createdBy cb " +
           "WHERE tq.question.id IN :questionIds " +
           "ORDER BY tq.question.id ASC, tq.displayOrder ASC")
    List<TailQuestion> findByQuestionIdsWithDetails(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT tq FROM TailQuestion tq " +
           "JOIN FETCH tq.question q " +
           "JOIN FETCH tq.createdBy cb " +
           "WHERE tq.id = :tailQuestionId")
    Optional<TailQuestion> findByIdWithDetails(@Param("tailQuestionId") Long tailQuestionId);

    @Query("SELECT COUNT(tq) FROM TailQuestion tq " +
           "WHERE tq.question.id = :questionId")
    long countByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT MAX(tq.displayOrder) FROM TailQuestion tq " +
           "WHERE tq.question.id = :questionId")
    Optional<Integer> findMaxDisplayOrderByQuestionId(@Param("questionId") Long questionId);
}