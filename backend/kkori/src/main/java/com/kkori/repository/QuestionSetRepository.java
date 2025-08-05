package com.kkori.repository;

import com.kkori.entity.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

    @Query("SELECT MAX(qs.versionNumber) FROM QuestionSet qs WHERE qs.parentVersionId.id = :parentVersionId OR qs.id = :parentVersionId")
    Optional<Integer> findMaxVersionNumberByParentVersionId(@Param("parentVersionId") Long parentVersionId);

}
