package com.kkori.repository;

import com.kkori.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 태그 이름으로 태그 조회
     */
    Optional<Tag> findByTag(String tagName);

    /**
     * 여러 태그 이름으로 태그 목록 조회
     */
    @Query("SELECT t FROM Tag t WHERE t.tag IN :tagNames")
    List<Tag> findByTagIn(@Param("tagNames") List<String> tagNames);

    /**
     * 태그 이름이 존재하는지 확인
     */
    boolean existsByTag(String tagName);
}