package com.kkori.service;

import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 질문 세트 서비스
 * 주요 기능:
 * - 불변 버전 관리 시스템을 통한 질문 세트 관리
 * - 질문, 답변, 꼬리질문의 독립적 관리
 * - 권한 기반 질문 세트 접근 제어
 * - 성능 최적화된 조회 기능
 */
public interface QuestionSetService {

    // ===============================================
    // CREATE OPERATIONS - 생성 작업
    // ===============================================

    /**
     * 질문과 답변을 포함한 질문 세트 생성
     */
    CreateQuestionSetResponse createQuestionSetWithQuestions(Long userId, CreateQuestionSetWithQuestionsRequest request);

    /**
     * 기존 질문 세트 복사
     */
    CreateQuestionSetResponse copyQuestionSet(Long userId, CopyQuestionSetRequest request);

    /**
     * 새 질문-답변으로 새 버전 생성
     */
    CreateQuestionSetResponse createVersionWithNewQA(Long userId, CreateVersionWithNewQARequest request);

    /**
     * 기존 질문 + 새 답변으로 새 버전 생성
     */
    CreateQuestionSetResponse createVersionWithAnswerModifications(Long userId, CreateVersionWithAnswerModificationsRequest request);

    /**
     * 기존 질문 수정 + 새 질문 추가로 새 버전 생성 (통합 API)
     */
    CreateQuestionSetResponse editQuestionSetVersion(Long userId, EditQuestionSetVersionRequest request);

    // ===============================================
    // READ OPERATIONS - 조회 작업
    // ===============================================

    /**
     * 필터링을 지원하는 질문 세트 목록 조회
     */
    Page<QuestionSetListResponse> getQuestionSetList(Long userId, int page, int size, String sort,
                                                   String createdBy, Boolean isPublic, List<String> tags);

    /**
     * 질문 세트 상세 조회 (새로운 구조)
     */
    QuestionSetDetailResponse getQuestionSetDetailNew(Long userId, Long questionSetId);

    /**
     * 내 질문 세트 목록 조회
     */
    Page<QuestionSetListResponse> getMyQuestionSets(Long userId, int page, int size);

    /**
     * 공개 질문 세트 목록 조회
     */
    Page<QuestionSetListResponse> getPublicQuestionSetsNew(Long userId, int page, int size);

    /**
     * 질문 세트 버전 히스토리 조회
     */
    List<QuestionSetListResponse> getQuestionSetVersionsNew(Long userId, Long questionSetId);

    // ===============================================
    // UPDATE OPERATIONS - 수정 작업
    // ===============================================

    /**
     * 질문 세트 메타데이터 수정
     */
    QuestionSetDetailResponse updateQuestionSetMetadata(Long userId, Long questionSetId, UpdateQuestionSetMetadataRequest request);

    // ===============================================
    // DELETE OPERATIONS - 삭제 작업
    // ===============================================

    /**
     * 질문 세트 Soft Delete
     */
    QuestionSetDetailResponse softDeleteQuestionSet(Long userId, Long questionSetId);

    /**
     * 질문 세트에서 특정 질문 제거
     */
    QuestionMapResponse removeQuestionFromSet(Long userId, Long questionSetId, Long mapId);

    // ===============================================
    // 기존 메서드들 (호환성 유지)
    // ===============================================

    /**
     * 초기 질문들과 함께 질문 세트 생성 (기존 메서드)
     */
    Long createQuestionSetWithInitialQuestions(Long userId, CreateNewQuestionSetRequest request, String title);

    /**
     * 빈 질문 세트 생성
     */
    Long createNewQuestionSet(Long userId, String title, String description);

    /**
     * 기존 질문 세트에 질문 추가
     */
    Long addQuestionToQuestionSet(Long userId, Long questionSetId, CreateQuestionRequest request);

    /**
     * 질문 세트 상세 조회 (기존 메서드)
     */
    QuestionSetResponse getQuestionSetDetail(Long userId, Long questionSetId);

    /**
     * 사용자의 질문 세트 목록 조회 (기존 메서드)
     */
    List<QuestionSetResponse> getUserQuestionSets(Long userId, int page, int size);

    /**
     * 질문 세트의 모든 버전 히스토리 조회 (기존 메서드)
     */
    List<QuestionSetResponse> getQuestionSetVersions(Long userId, Long questionSetId);

    /**
     * 공개된 질문 세트 목록 조회 (기존 메서드)
     */
    List<QuestionSetResponse> getPublicQuestionSets(Long userId, int page, int size);
}
