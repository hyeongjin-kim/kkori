package com.kkori.repository;

import com.kkori.entity.QuestionSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 질문 세트 Repository
 * 
 * 주요 기능:
 * - 불변 Question 재사용을 위한 효율적 조회
 * - 버전 관리 시스템 지원
 * - N+1 문제 방지를 위한 Fetch Join 쿼리
 * - 권한 기반 데이터 접근 제어
 */
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

    /**
     * 질문과 태그를 포함한 질문 세트 상세 조회 (N+1 방지)
     * MultipleBagFetchException 방지를 위해 두 단계로 분리
     */
    @Query("""
        SELECT DISTINCT qs FROM QuestionSet qs
        LEFT JOIN FETCH qs.questionMaps qm
        LEFT JOIN FETCH qm.question q
        WHERE qs.id = :questionSetId
        """)
    Optional<QuestionSet> findByIdWithQuestions(@Param("questionSetId") Long questionSetId);

    @Query("""
        SELECT qs FROM QuestionSet qs
        LEFT JOIN FETCH qs.questionSetTags qst
        LEFT JOIN FETCH qst.tag t
        WHERE qs.id = :questionSetId
        """)
    Optional<QuestionSet> findByIdWithTags(@Param("questionSetId") Long questionSetId);
    
    default Optional<QuestionSet> findByIdWithQuestionsAndTags(Long questionSetId) {
        Optional<QuestionSet> questionSetWithQuestions = findByIdWithQuestions(questionSetId);
        if (questionSetWithQuestions.isPresent()) {
            findByIdWithTags(questionSetId); // 태그도 로딩
            return questionSetWithQuestions;
        }
        return Optional.empty();
    }


    /**
     * 특정 질문 세트의 모든 버전 조회 (버전 관리 시스템)
     * 단순화된 접근: 루트 버전을 찾아서 모든 버전 조회
     */
    @Query("""
        SELECT qs FROM QuestionSet qs
        WHERE qs.ownerUserId.userId = :userId
        AND (qs.id = :questionSetId 
             OR qs.parentVersionId.id = :questionSetId
             OR qs.id IN (
                 SELECT parent.id FROM QuestionSet parent
                 WHERE parent.id IN (
                     SELECT child.parentVersionId.id FROM QuestionSet child
                     WHERE child.id = :questionSetId
                 )
             ))
        ORDER BY qs.versionNumber ASC
        """)
    List<QuestionSet> findAllVersionsByQuestionSetId(@Param("questionSetId") Long questionSetId, @Param("userId") Long userId);

    /**
     * 공개된 질문 세트 조회 (자신 제외)
     */
    @Query("""
        SELECT qs FROM QuestionSet qs
        WHERE qs.isPublic = true 
        AND qs.ownerUserId.userId != :userId
        ORDER BY qs.createdAt DESC
        """)
    List<QuestionSet> findPublicQuestionSets(@Param("userId") Long userId, Pageable pageable);

    /**
     * 버전 관리를 위한 최대 버전 번호 조회
     */
    @Query("SELECT MAX(qs.versionNumber) FROM QuestionSet qs WHERE qs.parentVersionId.id = :parentVersionId OR qs.id = :parentVersionId")
    Optional<Integer> findMaxVersionNumberByParentVersionId(@Param("parentVersionId") Long parentVersionId);

    /**
     * 특정 Question을 사용하는 모든 질문 세트 조회 (불변성 검증용)
     */
    @Query("""
        SELECT DISTINCT qs FROM QuestionSet qs
        JOIN qs.questionMaps qm
        WHERE qm.question.id = :questionId
        ORDER BY qs.createdAt DESC
        """)
    List<QuestionSet> findQuestionSetsByQuestionId(@Param("questionId") Long questionId);


    /**
     * 질문 세트 통계 조회 (대시보드용)
     */
    @Query("""
        SELECT qs.ownerUserId.userId, COUNT(qs.id) as questionSetCount, 
               COUNT(CASE WHEN qs.isPublic = true THEN 1 END) as publicCount,
               MAX(qs.versionNumber) as maxVersion
        FROM QuestionSet qs
        WHERE qs.ownerUserId.userId = :userId
        GROUP BY qs.ownerUserId.userId
        """)
    List<Object[]> findQuestionSetStatsByUserId(@Param("userId") Long userId);

    // ===== 새로운 불변 버전 관리 시스템용 메서드들 =====

    /**
     * 완전한 질문 세트 상세 조회 (모든 연관 엔티티 포함)
     */
    @Query("SELECT DISTINCT qs FROM QuestionSet qs " +
           "LEFT JOIN FETCH qs.questionSetTags qst " +
           "LEFT JOIN FETCH qst.tag " +
           "WHERE qs.id = :questionSetId AND qs.isDeleted = false")
    Optional<QuestionSet> findByIdWithAllDetails(@Param("questionSetId") Long questionSetId);

    /**
     * 페이징을 지원하는 질문 세트 목록 조회 (필터링 포함)
     */
    @Query("SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE (:userId IS NULL OR qs.ownerUserId.userId = :userId) " +
           "AND (:isPublic IS NULL OR qs.isPublic = :isPublic) " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.createdAt DESC")
    Page<QuestionSet> findQuestionSetsWithFilters(
            @Param("userId") Long userId, 
            @Param("isPublic") Boolean isPublic, 
            Pageable pageable);

    /**
     * 사용자 본인의 질문 세트 페이징 조회
     */
    @Query("SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE qs.ownerUserId.userId = :userId " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.versionNumber DESC, qs.createdAt DESC")
    Page<QuestionSet> findMyQuestionSets(@Param("userId") Long userId, Pageable pageable);

    /**
     * 공개된 질문 세트 페이징 조회 (본인 제외)
     */
    @Query("SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE qs.isPublic = true " +
           "AND qs.ownerUserId.userId != :userId " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.createdAt DESC")
    Page<QuestionSet> findPublicQuestionSetsWithPaging(@Param("userId") Long userId, Pageable pageable);

    /**
     * 태그 기반 질문 세트 검색
     */
    @Query("SELECT DISTINCT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "JOIN qs.questionSetTags qst " +
           "JOIN qst.tag t " +
           "WHERE t.tag IN :tagNames " +
           "AND (:userId IS NULL OR qs.ownerUserId.userId = :userId OR qs.isPublic = true) " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.createdAt DESC")
    Page<QuestionSet> findByTagNames(@Param("tagNames") List<String> tagNames,
                                                                    @Param("userId") Long userId, 
                                                                    Pageable pageable);

    /**
     * 특정 질문 세트의 버전 히스토리 조회
     */
    @Query("SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE (qs.id = :questionSetId OR qs.parentVersionId.id = :questionSetId) " +
           "AND qs.ownerUserId.userId = :userId " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.versionNumber ASC")
    List<QuestionSet> findVersionHistory(@Param("questionSetId") Long questionSetId, @Param("userId") Long userId);

    /**
     * Soft delete 상태 확인
     */
    @Query("SELECT qs FROM QuestionSet qs WHERE qs.id = :questionSetId AND qs.isDeleted = false")
    Optional<QuestionSet> findByIdAndNotDeleted(@Param("questionSetId") Long questionSetId);


    /**
     * 접근 가능한 모든 질문 세트 조회 (본인 + 공유된 것)
     */
    @Query("SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE (qs.ownerUserId.userId = :userId OR qs.isPublic = true) " +
           "AND qs.isDeleted = false " +
           "ORDER BY qs.createdAt DESC")
    Page<QuestionSet> findAccessibleQuestionSets(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 최근 생성된 공개 질문 세트 Top N 조회 (캐싱용)
     */
    @Query(value = "SELECT qs FROM QuestionSet qs " +
           "JOIN FETCH qs.ownerUserId " +
           "WHERE qs.isPublic = true AND qs.isDeleted = false " +
           "ORDER BY qs.createdAt DESC")
    List<QuestionSet> findTopRecentPublicQuestionSets(Pageable pageable);
    
    /**
     * 사용자별 질문 세트 개수 조회 (통계용)
     */
    @Query("SELECT COUNT(qs) FROM QuestionSet qs " +
           "WHERE qs.ownerUserId.userId = :userId AND qs.isDeleted = false")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 배치 삭제 최적화 - 여러 질문 세트 한 번에 soft delete
     */
    @Query("UPDATE QuestionSet qs SET qs.isDeleted = true " +
           "WHERE qs.id IN :questionSetIds AND qs.ownerUserId.userId = :userId")
    @org.springframework.data.jpa.repository.Modifying
    void softDeleteByIds(@Param("questionSetIds") List<Long> questionSetIds, 
                         @Param("userId") Long userId);

    /**
     * 사용자 본인의 질문 세트 최신 버전만 조회
     * 각 질문 세트 그룹(루트)별로 최신 버전만 반환
     */
    @Query("""
        SELECT qs FROM QuestionSet qs
        JOIN FETCH qs.ownerUserId
        WHERE qs.ownerUserId.userId = :userId
        AND qs.isDeleted = false
        AND qs.id IN (
            SELECT MAX(qs2.id) FROM QuestionSet qs2
            WHERE qs2.ownerUserId.userId = :userId
            AND qs2.isDeleted = false
            GROUP BY COALESCE(
                CASE WHEN qs2.parentVersionId IS NULL THEN qs2.id ELSE qs2.parentVersionId.id END,
                qs2.id
            )
        )
        ORDER BY qs.createdAt DESC
        """)
    Page<QuestionSet> findMyLatestQuestionSets(@Param("userId") Long userId, Pageable pageable);

}
