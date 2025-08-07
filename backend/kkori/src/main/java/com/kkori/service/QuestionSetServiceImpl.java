package com.kkori.service;

import static com.kkori.constant.QuestionSetConstants.DefaultValue.DEFAULT_SHARED_STATUS;
import static com.kkori.constant.QuestionSetConstants.DefaultValue.INITIAL_VERSION_NUMBER;
import static com.kkori.constant.QuestionSetConstants.DefaultValue.NEXT_VERSION_NUMBER;
import static com.kkori.entity.QuestionType.fromCode;
import static java.util.Comparator.comparingInt;

import com.kkori.dto.question.request.*;
import com.kkori.dto.question.response.*;
import com.kkori.entity.*;
import com.kkori.exception.questionset.QuestionSetException;
import com.kkori.exception.user.UserException;
import com.kkori.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuestionSetServiceImpl implements QuestionSetService {

    private final QuestionSetRepository questionSetRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionSetQuestionMapRepository questionSetQuestionMapRepository;
    private final TailQuestionRepository tailQuestionRepository;

    @Override
    public Long createQuestionSetWithInitialQuestions(Long userId, CreateNewQuestionSetRequest request, String title) {
        validateCreateQuestionSetRequest(request, title);

        if (request.getParentVersionId() != null) {
            return createVersionedQuestionSetFromParent(userId, request, title);
        }

        return createBrandNewQuestionSetWithQuestions(userId, request, title);
    }

    private void validateCreateQuestionSetRequest(CreateNewQuestionSetRequest request, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw QuestionSetException.noTitle();
        }
        validateQuestionsNotEmpty(request.getQuestions());
    }

    private void validateQuestionsNotEmpty(List<CreateQuestionRequest> questions) {
        Optional.ofNullable(questions)
                .filter(list -> !list.isEmpty())
                .orElseThrow(QuestionSetException::emptyQuestions);
    }

    @Override
    public Long createNewQuestionSet(Long userId, String title, String description) {
        User user = findUserById(userId);

        QuestionSet questionSet = createQuestionSetEntity(user, title, description, null, INITIAL_VERSION_NUMBER);
        QuestionSet savedQuestionSet = questionSetRepository.save(questionSet);
        
        return savedQuestionSet.getId();
    }

    private QuestionSet createQuestionSetEntity(User user, String title, String description, QuestionSet parentVersion, Integer versionNumber) {
        return QuestionSet.builder()
                .ownerUserId(user)
                .title(title)
                .description(description)
                .versionNumber(versionNumber)
                .parentVersionId(parentVersion)
                .isShared(DEFAULT_SHARED_STATUS)
                .build();
    }

    @Override
    public Long addQuestionToQuestionSet(Long userId, Long questionSetId, CreateQuestionRequest request) {

        User user = findUserById(userId);
        QuestionSet questionSet = findQuestionSetById(questionSetId);

        validateQuestionSetOwnership(questionSet, userId);

        Question savedQuestion = createAndSaveQuestion(request);
        Answer savedAnswer = Answer.create(request.getExpectedAnswer(), user);
        answerRepository.save(savedAnswer);
        
        int displayOrder = (int) (questionSetQuestionMapRepository.countByQuestionSetId(questionSetId) + 1);
        QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.create(
                questionSet, savedQuestion, savedAnswer, (int) displayOrder);
        questionSetQuestionMapRepository.save(questionMap);

        return savedQuestion.getId();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    private QuestionSet findQuestionSetById(Long questionSetId) {
        return questionSetRepository.findById(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
    }

    private void validateQuestionSetOwnership(QuestionSet questionSet, Long userId) {
        if (!questionSet.getOwnerUserId().getUserId().equals(userId)) {
            throw QuestionSetException.noPermission();
        }
    }

    private Question createAndSaveQuestion(CreateQuestionRequest request) {
        Question question = Question.builder()
                .content(request.getContent())
                .questionType(fromCode(request.getQuestionType()))
                .build();

        return questionRepository.save(question);
    }

    private Long createBrandNewQuestionSetWithQuestions(Long userId, CreateNewQuestionSetRequest request, String title) {
        Long questionSetId = createNewQuestionSet(userId, title, request.getDescription());
        addMultipleQuestionsToSet(userId, questionSetId, request.getQuestions());
        return questionSetId;
    }

    private Long createVersionedQuestionSetFromParent(Long userId, CreateNewQuestionSetRequest request, String title) {
        User user = findUserById(userId);
        QuestionSet parentQuestionSet = findQuestionSetById(request.getParentVersionId());
        
        validateQuestionSetOwnership(parentQuestionSet, userId);
        
        Integer nextVersionNumber = getNextVersionNumber(request.getParentVersionId());
        QuestionSet newVersionQuestionSet = createQuestionSetEntity(user, title, request.getDescription(), parentQuestionSet, nextVersionNumber);
        QuestionSet savedQuestionSet = questionSetRepository.save(newVersionQuestionSet);
        
        linkExistingQuestionsToNewVersion(parentQuestionSet, savedQuestionSet);
        addMultipleQuestionsToSet(userId, savedQuestionSet.getId(), request.getQuestions());
        
        return savedQuestionSet.getId();
    }
    
    private Integer getNextVersionNumber(Long parentVersionId) {
        return questionSetRepository.findMaxVersionNumberByParentVersionId(parentVersionId)
                .map(maxVersion -> maxVersion + 1)
                .orElse(NEXT_VERSION_NUMBER);
    }
    
    private void linkExistingQuestionsToNewVersion(QuestionSet parentQuestionSet, QuestionSet childQuestionSet) {
        parentQuestionSet.getQuestionMaps().stream()
                .sorted(comparingInt(QuestionSetQuestionMap::getDisplayOrder))
                .forEach(parentQuestionMap -> {
                    Question existingQuestion = parentQuestionMap.getQuestion();
                    Answer existingAnswer = parentQuestionMap.getAnswer();
                    
                    QuestionSetQuestionMap newMapping = QuestionSetQuestionMap.create(
                            childQuestionSet, 
                            existingQuestion, 
                            existingAnswer, 
                            parentQuestionMap.getDisplayOrder()
                    );
                    questionSetQuestionMapRepository.save(newMapping);
                });
    }
    

    private void addMultipleQuestionsToSet(Long userId, Long questionSetId, List<CreateQuestionRequest> questions) {
        questions.forEach(singleQuestionRequest -> addQuestionToQuestionSet(userId, questionSetId, singleQuestionRequest));
    }

    // 이전 메서드들은 더이상 사용하지 않음 - 새 구조에서는 Answer가 필요

    // ===============================================
    // 읽기 전용 메소드들 (조회 성능 최적화)
    // ===============================================

    /**
     * 질문 세트 상세 조회 (권한 검증 포함)
     * - N+1 문제 방지를 위한 Fetch Join 활용
     * - 소유자 권한 또는 공개 상태 검증
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public QuestionSetResponse getQuestionSetDetail(Long userId, Long questionSetId) {
        QuestionSet questionSet = questionSetRepository.findByIdWithQuestionsAndTags(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);

        validateQuestionSetAccessPermission(questionSet, userId);

        return convertToQuestionSetResponse(questionSet);
    }

    /**
     * 사용자의 질문 세트 목록 조회 (페이징)
     * - 최신 버전 우선 정렬
     * - 성능 최적화를 위해 질문 상세 정보는 제외
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<QuestionSetResponse> getUserQuestionSets(Long userId, int page, int size) {
        findUserById(userId); // 사용자 존재 검증
        
        Pageable pageable = PageRequest.of(page, size);
        List<QuestionSet> questionSets = questionSetRepository
                .findByOwnerUserIdOrderByVersionDesc(userId, pageable);

        return questionSets.stream()
                .map(this::convertToQuestionSetResponseWithoutDetails)
                .collect(Collectors.toList());
    }

    /**
     * 질문 세트의 모든 버전 히스토리 조회
     * - 버전 관리 시스템 활용
     * - 소유자만 접근 가능
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<QuestionSetResponse> getQuestionSetVersions(Long userId, Long questionSetId) {
        // 기본 권한 검증
        QuestionSet baseQuestionSet = questionSetRepository.findByIdWithQuestionsAndTags(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        validateQuestionSetOwnership(baseQuestionSet, userId);

        // 모든 버전 조회
        List<QuestionSet> allVersions = questionSetRepository
                .findAllVersionsByQuestionSetId(questionSetId, userId);

        return allVersions.stream()
                .map(this::convertToQuestionSetResponseWithoutDetails)
                .collect(Collectors.toList());
    }

    /**
     * 공유된 질문 세트 목록 조회 (자신 제외)
     * - 다른 사용자가 공개한 질문 세트만 조회
     * - 페이징 처리
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<QuestionSetResponse> getSharedQuestionSets(Long userId, int page, int size) {
        findUserById(userId); // 사용자 존재 검증
        
        Pageable pageable = PageRequest.of(page, size);
        List<QuestionSet> sharedQuestionSets = questionSetRepository
                .findSharedQuestionSets(userId, pageable);

        return sharedQuestionSets.stream()
                .map(this::convertToQuestionSetResponseWithoutDetails)
                .collect(Collectors.toList());
    }

    /**
     * 질문 세트 접근 권한 검증
     * - 소유자이거나 공개된 질문 세트인 경우만 접근 허용
     */
    private void validateQuestionSetAccessPermission(QuestionSet questionSet, Long userId) {
        boolean isOwner = questionSet.getOwnerUserId().getUserId().equals(userId);
        boolean isShared = questionSet.getIsShared();
        
        if (!isOwner && !isShared) {
            throw QuestionSetException.noPermission();
        }
    }

    /**
     * QuestionSet → QuestionSetResponse 변환 (상세 정보 포함)
     * - 질문 목록과 태그 정보 포함
     * - N+1 방지를 위해 이미 Fetch Join된 데이터 활용
     */
    private QuestionSetResponse convertToQuestionSetResponse(QuestionSet questionSet) {
        List<QuestionSummaryResponse> questions = questionSet.getQuestionMaps().stream()
                .sorted(comparingInt(QuestionSetQuestionMap::getDisplayOrder))
                .map(this::convertToQuestionSummaryResponse)
                .collect(Collectors.toList());

        List<TagResponse> tags = questionSet.getQuestionSetTags().stream()
                .map(qst -> TagResponse.builder()
                        .id(qst.getTag().getId())
                        .tag(qst.getTag().getTag())
                        .build())
                .collect(Collectors.toList());

        return QuestionSetResponse.builder()
                .id(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .versionNumber(questionSet.getVersionNumber())
                .parentVersionId(questionSet.getParentVersionId() != null ? 
                    questionSet.getParentVersionId().getId() : null)
                .isShared(questionSet.getIsShared())
                .ownerNickname(questionSet.getOwnerUserId().getNickname())
                .questions(questions)
                .tags(tags)
                .createdAt(questionSet.getCreatedAt())
                .build();
    }

    /**
     * QuestionSet → QuestionSetResponse 변환 (목록용 - 상세 정보 제외)
     * - 성능 최적화를 위해 질문 목록은 제외
     * - 목록 조회 시 사용
     */
    private QuestionSetResponse convertToQuestionSetResponseWithoutDetails(QuestionSet questionSet) {
        return QuestionSetResponse.builder()
                .id(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .versionNumber(questionSet.getVersionNumber())
                .parentVersionId(questionSet.getParentVersionId() != null ? 
                    questionSet.getParentVersionId().getId() : null)
                .isShared(questionSet.getIsShared())
                .ownerNickname(questionSet.getOwnerUserId().getNickname())
                .createdAt(questionSet.getCreatedAt())
                .build();
    }

    /**
     * QuestionSetQuestionMap → QuestionSummaryResponse 변환
     * - 불변 Question 정보를 DTO로 변환
     * - displayOrder 정보 포함
     */
    private QuestionSummaryResponse convertToQuestionSummaryResponse(QuestionSetQuestionMap questionMap) {
        Question question = questionMap.getQuestion();
        
        return QuestionSummaryResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .questionType(question.getQuestionType().getCode())
                .displayOrder(questionMap.getDisplayOrder())
                .build();
    }

    // ===============================================
    // 새로운 불변 버전 관리 시스템 메서드들
    // ===============================================

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 30
    )
    public CreateQuestionSetResponse createQuestionSetWithQuestions(Long userId, CreateQuestionSetWithQuestionsRequest request) {
        log.info("새로운 질문 세트 생성 시작 - userId: {}, title: {}", userId, request.getTitle());
        
        User user = findUserById(userId);
        
        // 1. 질문 세트 생성
        QuestionSet questionSet = QuestionSet.createNew(user, request.getTitle(), request.getDescription());
        QuestionSet savedQuestionSet = questionSetRepository.save(questionSet);
        
        // 2. 질문과 답변들 생성 및 매핑
        List<QuestionMapResponse> questionMaps = new ArrayList<>();
        List<TailQuestionResponse> tailQuestions = new ArrayList<>();
        
        int displayOrder = 1;
        for (CreateQuestionWithAnswerRequest questionRequest : request.getQuestions()) {
            // 질문 생성
            Question question = Question.builder()
                    .content(questionRequest.getContent())
                    .questionType(fromCode(questionRequest.getQuestionType()))
                    .build();
            Question savedQuestion = questionRepository.save(question);
            
            // 답변 생성
            Answer answer = Answer.create(questionRequest.getExpectedAnswer(), user);
            Answer savedAnswer = answerRepository.save(answer);
            
            // 질문-답변 매핑 생성
            QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.create(
                    savedQuestionSet, savedQuestion, savedAnswer, displayOrder);
            questionSetQuestionMapRepository.save(questionMap);
            
            questionMaps.add(convertToQuestionMapResponse(questionMap, savedQuestion, savedAnswer));
            
            // 꼬리질문들 생성
            if (questionRequest.getTailQuestions() != null) {
                int tailOrder = 1;
                for (CreateTailQuestionRequest tailRequest : questionRequest.getTailQuestions()) {
                    TailQuestion tailQuestion = TailQuestion.create(
                            tailRequest.getContent(), savedQuestion, user, tailOrder);
                    TailQuestion savedTailQuestion = tailQuestionRepository.save(tailQuestion);
                    
                    tailQuestions.add(convertToTailQuestionResponse(savedTailQuestion));
                    tailOrder++;
                }
            }
            
            displayOrder++;
        }
        
        log.info("질문 세트 생성 완료 - questionSetId: {}, 질문 수: {}", savedQuestionSet.getId(), questionMaps.size());
        
        return CreateQuestionSetResponse.builder()
                .questionSetId(savedQuestionSet.getId())
                .title(savedQuestionSet.getTitle())
                .description(savedQuestionSet.getDescription())
                .versionNumber(savedQuestionSet.getVersionNumber())
                .parentVersionId(null)
                .isShared(savedQuestionSet.getIsShared())
                .ownerNickname(user.getNickname())
                .questionMaps(questionMaps)
                .tailQuestions(tailQuestions)
                .tags(new ArrayList<>()) // TODO: 태그 처리
                .createdAt(savedQuestionSet.getCreatedAt())
                .updatedAt(savedQuestionSet.getUpdatedAt())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public QuestionSetDetailResponse getQuestionSetDetailNew(Long userId, Long questionSetId) {
        log.info("질문 세트 상세 조회 - userId: {}, questionSetId: {}", userId, questionSetId);
        
        QuestionSet questionSet = questionSetRepository.findByIdAndNotDeleted(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!questionSet.canBeAccessedBy(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 질문 매핑들 조회
        List<QuestionSetQuestionMap> questionMaps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(questionSetId);
        
        List<QuestionMapResponse> questionMapResponses = new ArrayList<>();
        List<TailQuestionResponse> allTailQuestions = new ArrayList<>();
        
        for (QuestionSetQuestionMap map : questionMaps) {
            questionMapResponses.add(convertToQuestionMapResponse(map, map.getQuestion(), map.getAnswer()));
            
            // 해당 질문의 꼬리질문들 조회
            List<TailQuestion> tailQuestions = tailQuestionRepository.findByQuestionIdWithDetails(map.getQuestion().getId());
            allTailQuestions.addAll(tailQuestions.stream()
                    .map(this::convertToTailQuestionResponse)
                    .collect(Collectors.toList()));
        }
        
        return QuestionSetDetailResponse.builder()
                .questionSetId(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .versionNumber(questionSet.getVersionNumber())
                .parentVersionId(questionSet.getParentVersionId() != null ? 
                        questionSet.getParentVersionId().getId() : null)
                .isShared(questionSet.getIsShared())
                .ownerNickname(questionSet.getOwnerUserId().getNickname())
                .questionMaps(questionMapResponses)
                .tailQuestions(allTailQuestions)
                .tags(new ArrayList<>()) // TODO: 태그 처리
                .createdAt(questionSet.getCreatedAt())
                .updatedAt(questionSet.getUpdatedAt())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<QuestionSetListResponse> getMyQuestionSets(Long userId, int page, int size) {
        findUserById(userId);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<QuestionSet> questionSets = questionSetRepository.findMyQuestionSets(userId, pageable);
        return questionSets.map(this::convertToQuestionSetListResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<QuestionSetListResponse> getSharedQuestionSetsNew(Long userId, int page, int size) {
        findUserById(userId);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<QuestionSet> questionSets = questionSetRepository.findSharedQuestionSetsWithPaging(userId, pageable);
        return questionSets.map(this::convertToQuestionSetListResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 30
    )
    public CreateQuestionSetResponse copyQuestionSet(Long userId, CopyQuestionSetRequest request) {
        log.info("질문 세트 복사 시작 - userId: {}, originalId: {}", userId, request.getOriginalQuestionSetId());
        
        User newOwner = findUserById(userId);
        
        // 1. 원본 질문 세트 조회 및 접근 권한 검증
        QuestionSet originalQuestionSet = questionSetRepository.findByIdAndNotDeleted(request.getOriginalQuestionSetId())
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!originalQuestionSet.canBeAccessedBy(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 2. 새로운 질문 세트 생성
        QuestionSet copiedQuestionSet = QuestionSet.copy(
                originalQuestionSet, 
                newOwner, 
                request.getTitle(), 
                request.getDescription()
        );
        QuestionSet savedQuestionSet = questionSetRepository.save(copiedQuestionSet);
        
        // 3. 원본의 질문-답변 매핑들 복사 (기존 엔티티 재사용 - 불변성 활용)
        List<QuestionSetQuestionMap> originalMaps = questionSetQuestionMapRepository
                .findByQuestionSetIdWithDetails(request.getOriginalQuestionSetId());
        
        List<QuestionMapResponse> questionMaps = new ArrayList<>();
        List<TailQuestionResponse> allTailQuestions = new ArrayList<>();
        
        for (QuestionSetQuestionMap originalMap : originalMaps) {
            // 새로운 매핑 생성 (기존 질문과 답변 재사용)
            QuestionSetQuestionMap newMapping = QuestionSetQuestionMap.create(
                    savedQuestionSet, 
                    originalMap.getQuestion(),  // 기존 질문 재사용
                    originalMap.getAnswer(),    // 기존 답변 재사용
                    originalMap.getDisplayOrder()
            );
            questionSetQuestionMapRepository.save(newMapping);
            
            questionMaps.add(convertToQuestionMapResponse(newMapping, originalMap.getQuestion(), originalMap.getAnswer()));
            
            // 꼬리질문들도 복사 (필요시)
            if (request.getCopyTags()) {
                List<TailQuestion> originalTailQuestions = tailQuestionRepository
                        .findByQuestionIdWithDetails(originalMap.getQuestion().getId());
                
                for (TailQuestion originalTail : originalTailQuestions) {
                    // 꼬리질문은 새로 생성 (내용만 복사)
                    TailQuestion copiedTailQuestion = TailQuestion.create(
                            originalTail.getContent(),
                            originalMap.getQuestion(),
                            newOwner,
                            originalTail.getDisplayOrder()
                    );
                    TailQuestion savedTailQuestion = tailQuestionRepository.save(copiedTailQuestion);
                    allTailQuestions.add(convertToTailQuestionResponse(savedTailQuestion));
                }
            }
        }
        
        log.info("질문 세트 복사 완료 - newQuestionSetId: {}, 복사된 질문 수: {}", 
                savedQuestionSet.getId(), questionMaps.size());
        
        return CreateQuestionSetResponse.builder()
                .questionSetId(savedQuestionSet.getId())
                .title(savedQuestionSet.getTitle())
                .description(savedQuestionSet.getDescription())
                .versionNumber(savedQuestionSet.getVersionNumber())
                .parentVersionId(originalQuestionSet.getId())  // 복사본의 부모는 원본
                .isShared(savedQuestionSet.getIsShared())
                .ownerNickname(newOwner.getNickname())
                .questionMaps(questionMaps)
                .tailQuestions(allTailQuestions)
                .tags(new ArrayList<>()) // TODO: 태그 복사 처리
                .createdAt(savedQuestionSet.getCreatedAt())
                .updatedAt(savedQuestionSet.getUpdatedAt())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 45
    )
    public CreateQuestionSetResponse createNewVersion(Long userId, CreateNewVersionRequest request) {
        log.info("새 버전 생성 시작 - userId: {}, parentId: {}", userId, request.getParentQuestionSetId());
        
        User user = findUserById(userId);
        
        // 1. 부모 질문 세트 조회 및 권한 검증
        QuestionSet parentQuestionSet = questionSetRepository.findByIdAndNotDeleted(request.getParentQuestionSetId())
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!parentQuestionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 2. 새 버전 생성
        Integer nextVersionNumber = getNextVersionNumber(request.getParentQuestionSetId());
        QuestionSet newVersionQuestionSet = QuestionSet.createVersion(
                parentQuestionSet, user, 
                parentQuestionSet.getTitle(),  // 제목은 부모와 동일
                parentQuestionSet.getDescription()  // 설명도 부모와 동일
        );
        QuestionSet savedQuestionSet = questionSetRepository.save(newVersionQuestionSet);
        
        // 3. 요청에 따라 질문들 처리
        List<QuestionMapResponse> questionMaps = new ArrayList<>();
        List<TailQuestionResponse> allTailQuestions = new ArrayList<>();
        
        for (QuestionVersionRequest questionRequest : request.getQuestions()) {
            if (questionRequest.isReuse()) {
                // 기존 질문-답변 재사용
                Question existingQuestion = questionRepository.findById(questionRequest.getQuestionId())
                        .orElseThrow(QuestionSetException::questionSetNotFound);
                Answer existingAnswer = answerRepository.findByIdWithCreatedBy(questionRequest.getAnswerId())
                        .orElseThrow(QuestionSetException::questionSetNotFound);
                
                QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.create(
                        savedQuestionSet, existingQuestion, existingAnswer, questionRequest.getDisplayOrder());
                questionSetQuestionMapRepository.save(questionMap);
                
                questionMaps.add(convertToQuestionMapResponse(questionMap, existingQuestion, existingAnswer));
                
            } else if (questionRequest.isNewQuestion()) {
                // 새 질문-답변 생성
                Question newQuestion = Question.builder()
                        .content(questionRequest.getContent())
                        .questionType(fromCode(questionRequest.getQuestionType()))
                        .build();
                Question savedQuestion = questionRepository.save(newQuestion);
                
                Answer newAnswer = Answer.create(questionRequest.getExpectedAnswer(), user);
                Answer savedAnswer = answerRepository.save(newAnswer);
                
                QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.create(
                        savedQuestionSet, savedQuestion, savedAnswer, questionRequest.getDisplayOrder());
                questionSetQuestionMapRepository.save(questionMap);
                
                questionMaps.add(convertToQuestionMapResponse(questionMap, savedQuestion, savedAnswer));
                
            } else if (questionRequest.isModifyAnswer()) {
                // 기존 질문 + 새 답변
                Question existingQuestion = questionRepository.findById(questionRequest.getQuestionId())
                        .orElseThrow(QuestionSetException::questionSetNotFound);
                
                Answer newAnswer = Answer.create(questionRequest.getNewExpectedAnswer(), user);
                Answer savedAnswer = answerRepository.save(newAnswer);
                
                QuestionSetQuestionMap questionMap = QuestionSetQuestionMap.create(
                        savedQuestionSet, existingQuestion, savedAnswer, questionRequest.getDisplayOrder());
                questionSetQuestionMapRepository.save(questionMap);
                
                questionMaps.add(convertToQuestionMapResponse(questionMap, existingQuestion, savedAnswer));
            }
        }
        
        log.info("새 버전 생성 완료 - newQuestionSetId: {}, version: {}, 질문 수: {}", 
                savedQuestionSet.getId(), nextVersionNumber, questionMaps.size());
        
        return CreateQuestionSetResponse.builder()
                .questionSetId(savedQuestionSet.getId())
                .title(savedQuestionSet.getTitle())
                .description(savedQuestionSet.getDescription())
                .versionNumber(nextVersionNumber)
                .parentVersionId(parentQuestionSet.getId())
                .isShared(savedQuestionSet.getIsShared())
                .ownerNickname(user.getNickname())
                .questionMaps(questionMaps)
                .tailQuestions(allTailQuestions)
                .tags(new ArrayList<>()) // TODO: 태그 처리
                .createdAt(savedQuestionSet.getCreatedAt())
                .updatedAt(savedQuestionSet.getUpdatedAt())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<QuestionSetListResponse> getQuestionSetList(Long userId, int page, int size, String sort, String createdBy, Boolean isShared, List<String> tags) {
        log.info("질문 세트 목록 조회 - userId: {}, page: {}, size: {}, createdBy: {}, isShared: {}, tags: {}", 
                userId, page, size, createdBy, isShared, tags);
        
        findUserById(userId);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<QuestionSet> questionSets;
        
        if ("me".equals(createdBy)) {
            // 내가 생성한 질문 세트만 조회
            questionSets = questionSetRepository.findMyQuestionSets(userId, pageable);
        } else if (isShared != null && isShared) {
            // 공유된 질문 세트 조회 (본인 제외)
            questionSets = questionSetRepository.findSharedQuestionSetsWithPaging(userId, pageable);
        } else if (tags != null && !tags.isEmpty()) {
            // 태그로 필터링된 질문 세트 조회
            questionSets = questionSetRepository.findByTagsWithPaging(userId, tags, pageable);
        } else {
            // 전체 질문 세트 조회 (접근 가능한 것만)
            questionSets = questionSetRepository.findAccessibleQuestionSets(userId, pageable);
        }
        
        return questionSets.map(this::convertToQuestionSetListResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<QuestionSetListResponse> getQuestionSetVersionsNew(Long userId, Long questionSetId) {
        log.info("질문 세트 버전 히스토리 조회 - userId: {}, questionSetId: {}", userId, questionSetId);
        
        // 기본 권한 검증
        QuestionSet baseQuestionSet = questionSetRepository.findByIdAndNotDeleted(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!baseQuestionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 모든 버전 조회 (부모-자식 관계 포함)
        List<QuestionSet> allVersions = questionSetRepository
                .findAllVersionsByQuestionSetId(questionSetId, userId);
        
        return allVersions.stream()
                .map(this::convertToQuestionSetListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 15
    )
    public QuestionSetDetailResponse updateQuestionSetMetadata(Long userId, Long questionSetId, UpdateQuestionSetMetadataRequest request) {
        log.info("질문 세트 메타데이터 수정 - userId: {}, questionSetId: {}", userId, questionSetId);
        
        QuestionSet questionSet = questionSetRepository.findByIdAndNotDeleted(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!questionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 메타데이터 수정
        if (request.getTitle() != null) {
            // 새로운 QuestionSet으로 업데이트 (불변성 원칙에 따라)
            QuestionSet updatedQuestionSet = QuestionSet.builder()
                    .ownerUserId(questionSet.getOwnerUserId())
                    .title(request.getTitle())
                    .description(request.getDescription() != null ? request.getDescription() : questionSet.getDescription())
                    .versionNumber(questionSet.getVersionNumber())
                    .parentVersionId(questionSet.getParentVersionId())
                    .isShared(request.getIsShared() != null ? request.getIsShared() : questionSet.getIsShared())
                    .build();
            
            questionSetRepository.save(updatedQuestionSet);
            questionSet = updatedQuestionSet;
        } else {
            // 기존 객체 필드만 업데이트
            if (request.getDescription() != null) {
                questionSet = QuestionSet.builder()
                        .ownerUserId(questionSet.getOwnerUserId())
                        .title(questionSet.getTitle())
                        .description(request.getDescription())
                        .versionNumber(questionSet.getVersionNumber())
                        .parentVersionId(questionSet.getParentVersionId())
                        .isShared(questionSet.getIsShared())
                        .build();
                questionSetRepository.save(questionSet);
            }
            
            if (request.getIsShared() != null) {
                questionSet.updateSharedStatus(request.getIsShared());
                questionSetRepository.save(questionSet);
            }
        }
        
        log.info("메타데이터 수정 완료 - questionSetId: {}", questionSet.getId());
        
        return getQuestionSetDetailNew(userId, questionSet.getId());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 15
    )
    public QuestionMapResponse modifyAnswer(Long userId, Long questionSetId, ModifyAnswerRequest request) {
        log.info("답변 수정 - userId: {}, questionSetId: {}, mapId: {}", userId, questionSetId, request.getMapId());
        
        User user = findUserById(userId);
        
        // 1. 질문 세트 및 매핑 조회
        QuestionSet questionSet = questionSetRepository.findByIdAndNotDeleted(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!questionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        QuestionSetQuestionMap originalMap = questionSetQuestionMapRepository.findByIdWithDetails(request.getMapId())
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        // 2. 해당 매핑이 현재 질문 세트에 속하는지 확인
        if (!originalMap.getQuestionSet().getId().equals(questionSetId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 3. 새로운 Answer 엔티티 생성 (불변성 유지)
        Answer newAnswer = Answer.create(request.getNewExpectedAnswer(), user);
        Answer savedAnswer = answerRepository.save(newAnswer);
        
        // 4. 기존 매핑 삭제하고 새 매핑 생성
        questionSetQuestionMapRepository.delete(originalMap);
        
        QuestionSetQuestionMap newMapping = QuestionSetQuestionMap.create(
                questionSet,
                originalMap.getQuestion(),  // 질문은 기존 것 재사용
                savedAnswer,  // 새 답변 사용
                originalMap.getDisplayOrder()  // 순서 유지
        );
        QuestionSetQuestionMap savedMapping = questionSetQuestionMapRepository.save(newMapping);
        
        log.info("답변 수정 완료 - newAnswerId: {}, originalAnswerId: {}", 
                savedAnswer.getId(), originalMap.getAnswer().getId());
        
        return convertToQuestionMapResponse(savedMapping, originalMap.getQuestion(), savedAnswer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 10
    )
    public QuestionSetDetailResponse softDeleteQuestionSet(Long userId, Long questionSetId) {
        log.info("질문 세트 Soft Delete - userId: {}, questionSetId: {}", userId, questionSetId);
        
        // 1. 질문 세트 조회 (이미 삭제된 것도 포함해서 조회해야 함)
        QuestionSet questionSet = questionSetRepository.findById(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        // 2. 소유권 검증
        if (!questionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 3. 이미 삭제된 상태인지 확인
        if (questionSet.getIsDeleted()) {
            log.warn("이미 삭제된 질문 세트 - questionSetId: {}", questionSetId);
            throw QuestionSetException.questionSetNotFound();
        }
        
        // 4. Soft Delete 수행
        questionSet.softDelete();
        questionSetRepository.save(questionSet);
        
        log.info("질문 세트 Soft Delete 완료 - questionSetId: {}", questionSetId);
        
        // 5. 삭제 상태의 상세 정보 반환 (관리자 목적)
        return QuestionSetDetailResponse.builder()
                .questionSetId(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .versionNumber(questionSet.getVersionNumber())
                .parentVersionId(questionSet.getParentVersionId() != null ? 
                        questionSet.getParentVersionId().getId() : null)
                .isShared(questionSet.getIsShared())
                .ownerNickname(questionSet.getOwnerUserId().getNickname())
                .questionMaps(new ArrayList<>()) // 삭제된 상태이므로 빈 목록
                .tailQuestions(new ArrayList<>()) // 삭제된 상태이므로 빈 목록
                .tags(new ArrayList<>())
                .createdAt(questionSet.getCreatedAt())
                .updatedAt(questionSet.getUpdatedAt())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(
        isolation = Isolation.READ_COMMITTED, 
        rollbackFor = Exception.class,
        timeout = 15
    )
    public QuestionMapResponse removeQuestionFromSet(Long userId, Long questionSetId, Long mapId) {
        log.info("질문 제거 - userId: {}, questionSetId: {}, mapId: {}", userId, questionSetId, mapId);
        
        // 1. 질문 세트 권한 검증
        QuestionSet questionSet = questionSetRepository.findByIdAndNotDeleted(questionSetId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!questionSet.isOwner(userId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 2. 매핑 조회 및 검증
        QuestionSetQuestionMap questionMap = questionSetQuestionMapRepository.findByIdWithDetails(mapId)
                .orElseThrow(QuestionSetException::questionSetNotFound);
        
        if (!questionMap.getQuestionSet().getId().equals(questionSetId)) {
            throw QuestionSetException.noPermission();
        }
        
        // 3. 제거하기 전 정보 저장 (응답용)
        QuestionMapResponse removedQuestionInfo = convertToQuestionMapResponse(
                questionMap, questionMap.getQuestion(), questionMap.getAnswer());
        
        // 4. 매핑 제거
        questionSetQuestionMapRepository.delete(questionMap);
        
        // 5. 남은 질문들의 displayOrder 재정렬
        List<QuestionSetQuestionMap> remainingMaps = questionSetQuestionMapRepository
                .findByQuestionSetIdOrderByDisplayOrder(questionSetId);
        
        int newOrder = 1;
        for (QuestionSetQuestionMap map : remainingMaps) {
            map.updateDisplayOrder(newOrder++);
            questionSetQuestionMapRepository.save(map);
        }
        
        log.info("질문 제거 완료 - removedMapId: {}, 남은 질문 수: {}", mapId, remainingMaps.size() - 1);
        
        return removedQuestionInfo;
    }

    // ===============================================
    // 헬퍼 메서드들
    // ===============================================

    private QuestionMapResponse convertToQuestionMapResponse(QuestionSetQuestionMap map, Question question, Answer answer) {
        return QuestionMapResponse.builder()
                .mapId(map.getId())
                .questionId(question.getId())
                .answerId(answer.getId())
                .displayOrder(map.getDisplayOrder())
                .question(QuestionDetailResponse.builder()
                        .id(question.getId())
                        .content(question.getContent())
                        .questionType(question.getQuestionType().getCode())
                        .createdBy("김개발") // TODO: 실제 사용자 이름
                        .createdAt(question.getCreatedAt())
                        .build())
                .answer(AnswerDetailResponse.builder()
                        .id(answer.getId())
                        .content(answer.getContent())
                        .createdByNickname(answer.getCreatedBy().getNickname())
                        .createdAt(answer.getCreatedAt())
                        .build())
                .build();
    }

    private TailQuestionResponse convertToTailQuestionResponse(TailQuestion tailQuestion) {
        return TailQuestionResponse.builder()
                .id(tailQuestion.getId())
                .content(tailQuestion.getContent())
                .questionId(tailQuestion.getQuestion().getId())
                .createdBy(tailQuestion.getCreatedBy().getNickname())
                .userAnswer(tailQuestion.getUserAnswer())
                .displayOrder(tailQuestion.getDisplayOrder())
                .createdAt(tailQuestion.getCreatedAt())
                .answeredAt(tailQuestion.getAnsweredAt())
                .build();
    }

    private QuestionSetListResponse convertToQuestionSetListResponse(QuestionSet questionSet) {
        // 질문 개수 조회
        long questionCount = questionSetQuestionMapRepository.countByQuestionSetId(questionSet.getId());
        
        return QuestionSetListResponse.builder()
                .questionSetId(questionSet.getId())
                .title(questionSet.getTitle())
                .description(questionSet.getDescription())
                .versionNumber(questionSet.getVersionNumber())
                .parentVersionId(questionSet.getParentVersionId() != null ? 
                        questionSet.getParentVersionId().getId() : null)
                .isShared(questionSet.getIsShared())
                .ownerNickname(questionSet.getOwnerUserId().getNickname())
                .questionCount((int) questionCount)
                .tags(new ArrayList<>()) // TODO: 태그 처리
                .createdAt(questionSet.getCreatedAt())
                .lastModifiedAt(questionSet.getUpdatedAt())
                .build();
    }

}
