package com.kkori.repository;

import com.kkori.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a " +
           "JOIN FETCH a.createdBy " +
           "WHERE a.id = :answerId")
    Optional<Answer> findByIdWithCreatedBy(@Param("answerId") Long answerId);

    @Query("SELECT a FROM Answer a " +
           "JOIN FETCH a.createdBy " +
           "WHERE a.id IN :answerIds")
    List<Answer> findByIdsWithCreatedBy(@Param("answerIds") List<Long> answerIds);

    @Query("SELECT a FROM Answer a " +
           "JOIN FETCH a.createdBy " +
           "WHERE a.createdBy.userId = :userId " +
           "ORDER BY a.createdAt DESC")
    List<Answer> findByCreatedByUserId(@Param("userId") Long userId);
}
