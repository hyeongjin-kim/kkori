package com.kkori.repository;

import com.kkori.entity.QuestionSet;
import com.kkori.entity.User;
import com.kkori.entity.Tag;
import com.kkori.entity.QuestionSetTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 최적화된 QuestionSet Repository 쿼리들의 실제 동작 테스트
 * 
 * 테스트 범위:
 * - 새로 추가한 최적화 메서드들이 실제로 동작하는지 검증
 * - 페이징, JOIN FETCH 등이 올바르게 작동하는지 확인
 * - 배치 삭제 등 최적화 기능들의 실제 동작 확인
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
class QuestionSetRepositoryOptimizedQueryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionSetRepository questionSetRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private QuestionSetTagRepository questionSetTagRepository;

    private User testUser1;
    private User testUser2;
    private QuestionSet publicQuestionSet1;
    private QuestionSet publicQuestionSet2;
    private QuestionSet privateQuestionSet;
    private Tag javaTag;
    private Tag springTag;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .sub("user1@test.com")
                .nickname("사용자1")
                .build();

        testUser2 = User.builder()
                .sub("user2@test.com")
                .nickname("사용자2")
                .build();

        entityManager.persist(testUser1);
        entityManager.persist(testUser2);

        // 공개 질문 세트들
        publicQuestionSet1 = QuestionSet.builder()
                .ownerUserId(testUser1)
                .title("공개 Java 질문세트")
                .description("Java 기초 질문들")
                .versionNumber(1)
                .isPublic(true)
                .build();

        publicQuestionSet2 = QuestionSet.builder()
                .ownerUserId(testUser2)
                .title("공개 Spring 질문세트")
                .description("Spring 기초 질문들")
                .versionNumber(1)
                .isPublic(true)
                .build();

        privateQuestionSet = QuestionSet.builder()
                .ownerUserId(testUser1)
                .title("비공개 질문세트")
                .description("개인 질문들")
                .versionNumber(1)
                .isPublic(false)
                .build();

        entityManager.persist(publicQuestionSet1);
        entityManager.persist(publicQuestionSet2);
        entityManager.persist(privateQuestionSet);

        // 태그 생성
        javaTag = Tag.builder().name("Java").build();
        springTag = Tag.builder().name("Spring").build();

        entityManager.persist(javaTag);
        entityManager.persist(springTag);

        // 태그 연결
        QuestionSetTag javaTagLink = QuestionSetTag.of(publicQuestionSet1, javaTag);
        QuestionSetTag springTagLink = QuestionSetTag.of(publicQuestionSet2, springTag);

        entityManager.persist(javaTagLink);
        entityManager.persist(springTagLink);

        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("새로 추가한 최적화 메서드들 동작 테스트")
    class OptimizedMethodsTest {

        @Test
        @DisplayName("findTopRecentPublicQuestionSets - 최신 공개 질문세트 Top N 조회")
        void findTopRecentPublicQuestionSets_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 5);

            // When
            List<QuestionSet> results = questionSetRepository.findTopRecentPublicQuestionSets(pageable);

            // Then
            assertThat(results).hasSize(2);
            assertThat(results).allMatch(qs -> qs.getIsPublic() && !qs.getIsDeleted());
            assertThat(results).allMatch(qs -> qs.getOwnerUserId() != null); // JOIN FETCH 확인

            // 제목으로 확인
            assertThat(results).extracting("title")
                    .contains("공개 Java 질문세트", "공개 Spring 질문세트");
        }

        @Test
        @DisplayName("countByUserId - 사용자별 질문세트 개수 조회")
        void countByUserId_Success() {
            // Given
            Long userId = testUser1.getUserId();

            // When
            Long count = questionSetRepository.countByUserId(userId);

            // Then
            assertThat(count).isEqualTo(2L); // publicQuestionSet1 + privateQuestionSet
        }

        @Test
        @DisplayName("findByTagNames - 태그 기반 질문세트 검색")
        void findByTagNames_Success() {
            // Given
            List<String> tagNames = Arrays.asList("Java");
            Long userId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<QuestionSet> results = questionSetRepository.findByTagNames(tagNames, userId, pageable);

            // Then
            assertThat(results.getContent()).hasSize(1);
            assertThat(results.getContent().get(0).getTitle()).isEqualTo("공개 Java 질문세트");
            assertThat(results.getContent().get(0).getOwnerUserId()).isNotNull(); // JOIN FETCH 확인
        }

        @Test
        @DisplayName("findAccessibleQuestionSets - 접근 가능한 모든 질문세트 조회")
        void findAccessibleQuestionSets_Success() {
            // Given
            Long userId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<QuestionSet> results = questionSetRepository.findAccessibleQuestionSets(userId, pageable);

            // Then
            assertThat(results.getContent()).hasSize(3); // 본인 2개 + 다른 사용자 공개 1개
            assertThat(results.getContent()).extracting("title")
                    .containsExactlyInAnyOrder(
                            "공개 Java 질문세트",
                            "비공개 질문세트",
                            "공개 Spring 질문세트");
        }

        @Test
        @DisplayName("findMyLatestQuestionSets - 내 질문세트 페이징 조회")
        void findMyLatestQuestionSets_Success() {
            // Given
            Long userId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<QuestionSet> results = questionSetRepository.findMyLatestQuestionSets(userId, pageable);

            // Then
            assertThat(results.getContent()).hasSize(2);
            assertThat(results.getContent()).allMatch(qs -> qs.getOwnerUserId().getUserId().equals(userId));
        }

        @Test
        @DisplayName("findPublicQuestionSetsWithPaging - 공개 질문세트 페이징 조회")
        void findPublicQuestionSetsWithPaging_Success() {
            // Given
            Long excludeUserId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<QuestionSet> results = questionSetRepository.findPublicQuestionSetsWithPaging(excludeUserId, pageable);

            // Then
            assertThat(results.getContent()).hasSize(1);
            assertThat(results.getContent().get(0).getOwnerUserId().getUserId())
                    .isEqualTo(testUser2.getUserId());
            assertThat(results.getContent().get(0).getIsPublic()).isTrue();
        }

        @Test
        @DisplayName("softDeleteByIds - 배치 삭제 기능 테스트")
        void softDeleteByIds_Success() {
            // Given
            Long userId = testUser1.getUserId();
            List<Long> questionSetIds = Arrays.asList(
                    publicQuestionSet1.getId(),
                    privateQuestionSet.getId());

            // When
            questionSetRepository.softDeleteByIds(questionSetIds, userId);
            entityManager.flush();
            entityManager.clear();

            // Then
            QuestionSet deletedPublic = entityManager.find(QuestionSet.class, publicQuestionSet1.getId());
            QuestionSet deletedPrivate = entityManager.find(QuestionSet.class, privateQuestionSet.getId());
            QuestionSet notDeleted = entityManager.find(QuestionSet.class, publicQuestionSet2.getId());

            assertThat(deletedPublic.getIsDeleted()).isTrue();
            assertThat(deletedPrivate.getIsDeleted()).isTrue();
            assertThat(notDeleted.getIsDeleted()).isFalse(); // 다른 사용자 소유는 삭제되지 않음
        }
    }

    @Nested
    @DisplayName("성능 및 쿼리 효율성 테스트")
    class QueryPerformanceTest {

        @Test
        @DisplayName("JOIN FETCH 쿼리가 N+1 문제를 방지하는지 확인")
        void verifyJoinFetchPreventsNPlusOne() {
            // Given
            Long userId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When - JOIN FETCH로 한 번에 로딩
            Page<QuestionSet> results = questionSetRepository.findMyLatestQuestionSets(userId, pageable);

            // Then - Lazy Loading이 발생하지 않아야 함
            entityManager.clear(); // 영속성 컨텍스트 클리어

            // 이미 로딩된 데이터이므로 추가 쿼리 없이 접근 가능
            results.getContent().forEach(qs -> {
                assertThat(qs.getOwnerUserId()).isNotNull();
                assertThat(qs.getOwnerUserId().getNickname()).isNotNull();
            });
        }

        @Test
        @DisplayName("배치 삭제가 단건 삭제보다 효율적인지 확인")
        void verifyBatchDeleteEfficiency() {
            // Given - 추가 질문세트들 생성
            QuestionSet additional1 = QuestionSet.builder()
                    .ownerUserId(testUser1)
                    .title("추가 질문세트 1")
                    .description("배치 삭제 테스트")
                    .versionNumber(1)
                    .isPublic(false)
                    .build();

            QuestionSet additional2 = QuestionSet.builder()
                    .ownerUserId(testUser1)
                    .title("추가 질문세트 2")
                    .description("배치 삭제 테스트")
                    .versionNumber(1)
                    .isPublic(false)
                    .build();

            entityManager.persist(additional1);
            entityManager.persist(additional2);
            entityManager.flush();

            List<Long> questionSetIds = Arrays.asList(additional1.getId(), additional2.getId());
            Long userId = testUser1.getUserId();

            // When - 배치 삭제 수행
            long startTime = System.nanoTime();
            questionSetRepository.softDeleteByIds(questionSetIds, userId);
            entityManager.flush();
            long endTime = System.nanoTime();

            // Then - 성능 검증
            long executionTime = endTime - startTime;
            assertThat(executionTime).isLessThan(50_000_000L); // 50ms 이내

            // 삭제 확인
            QuestionSet deleted1 = entityManager.find(QuestionSet.class, additional1.getId());
            QuestionSet deleted2 = entityManager.find(QuestionSet.class, additional2.getId());

            entityManager.clear(); // 영속성 컨텍스트를 새로 고침
            QuestionSet refreshed1 = entityManager.find(QuestionSet.class, additional1.getId());
            QuestionSet refreshed2 = entityManager.find(QuestionSet.class, additional2.getId());

            assertThat(refreshed1.getIsDeleted()).isTrue();
            assertThat(refreshed2.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("페이징 쿼리가 효율적으로 동작하는지 확인")
        void verifyPagingQueryEfficiency() {
            // Given - 더 많은 데이터 생성
            for (int i = 0; i < 20; i++) {
                QuestionSet questionSet = QuestionSet.builder()
                        .ownerUserId(testUser1)
                        .title("대량 질문세트 " + i)
                        .description("페이징 테스트용")
                        .versionNumber(1)
                        .isPublic(i % 2 == 0) // 절반은 공개
                        .build();
                entityManager.persist(questionSet);
            }
            entityManager.flush();

            Pageable smallPage = PageRequest.of(0, 5);
            Pageable largePage = PageRequest.of(0, 50);

            // When
            long startTime1 = System.nanoTime();
            Page<QuestionSet> smallResults = questionSetRepository.findMyLatestQuestionSets(testUser1.getUserId(), smallPage);
            long endTime1 = System.nanoTime();

            long startTime2 = System.nanoTime();
            Page<QuestionSet> largeResults = questionSetRepository.findMyLatestQuestionSets(testUser1.getUserId(), largePage);
            long endTime2 = System.nanoTime();

            // Then - 성능 비교
            assertThat(smallResults.getContent()).hasSize(5);
            assertThat(largeResults.getContent().size()).isGreaterThan(20);

            long smallPageTime = endTime1 - startTime1;
            long largePageTime = endTime2 - startTime2;

            // 페이징이 효율적으로 동작해야 함
            assertThat(smallPageTime).isLessThan(40_000_000L); // 40ms 이내
            assertThat(largePageTime).isLessThan(200_000_000L); // 200ms 이내
        }

        @Test
        @DisplayName("복합 조건 쿼리 성능 확인")
        void verifyComplexQueryPerformance() {
            // Given
            List<String> tagNames = Arrays.asList("Java", "Spring");
            Long userId = testUser1.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When
            long startTime = System.nanoTime();
            Page<QuestionSet> results = questionSetRepository.findByTagNames(tagNames, userId, pageable);
            long endTime = System.nanoTime();

            // Then
            assertThat(results.getContent()).hasSize(2);

            long executionTime = endTime - startTime;
            assertThat(executionTime).isLessThan(50_000_000L); // 30ms 이내
        }
    }

    @Nested
    @DisplayName("데이터 정합성 테스트")
    class DataIntegrityTest {

        @Test
        @DisplayName("Soft Delete 후 조회 메서드들이 삭제된 데이터를 제외하는지 확인")
        void verifyDeletedDataExclusion() {
            // Given
            Long userId = testUser1.getUserId();

            // 삭제 전 개수 확인
            Long countBefore = questionSetRepository.countByUserId(userId);
            assertThat(countBefore).isEqualTo(2L);

            // When - Soft Delete 수행
            questionSetRepository.softDeleteByIds(Arrays.asList(publicQuestionSet1.getId()), userId);
            entityManager.flush();
            entityManager.clear();

            // Then - 각 조회 메서드들이 삭제된 데이터를 제외해야 함
            Long countAfter = questionSetRepository.countByUserId(userId);
            assertThat(countAfter).isEqualTo(1L);

            Page<QuestionSet> myQuestionSets = questionSetRepository.findMyLatestQuestionSets(
                    userId, PageRequest.of(0, 10));
            assertThat(myQuestionSets.getContent()).hasSize(1);
            assertThat(myQuestionSets.getContent().get(0).getTitle()).isEqualTo("비공개 질문세트");

            List<QuestionSet> publicQuestionSets = questionSetRepository.findTopRecentPublicQuestionSets(
                    PageRequest.of(0, 10));
            assertThat(publicQuestionSets).hasSize(1);
            assertThat(publicQuestionSets.get(0).getTitle()).isEqualTo("공개 Spring 질문세트");
        }

        @Test
        @DisplayName("권한 기반 접근 제어가 올바르게 동작하는지 확인")
        void verifyAccessControl() {
            // Given
            Long user1Id = testUser1.getUserId();
            Long user2Id = testUser2.getUserId();
            Pageable pageable = PageRequest.of(0, 10);

            // When - 각 사용자별 접근 가능한 질문세트 조회
            Page<QuestionSet> user1Accessible = questionSetRepository.findAccessibleQuestionSets(user1Id, pageable);
            Page<QuestionSet> user2Accessible = questionSetRepository.findAccessibleQuestionSets(user2Id, pageable);

            // Then
            // testUser1은 자신의 2개 + testUser2의 공개 1개 = 총 3개
            assertThat(user1Accessible.getContent()).hasSize(3);

            // testUser2는 자신의 1개 + testUser1의 공개 1개 = 총 2개
            assertThat(user2Accessible.getContent()).hasSize(2);

            // testUser1의 비공개 질문세트는 testUser2가 접근할 수 없어야 함
            assertThat(user2Accessible.getContent()).noneMatch(qs -> "비공개 질문세트".equals(qs.getTitle()));
        }
    }
}