//package com.kkori.service;
//
//import com.kkori.component.InterviewRoomManager;
//import com.kkori.component.Transcriber;
//import com.kkori.component.TailQuestionGenerator;
//import com.kkori.component.interview.InterviewRoom;
//import com.kkori.component.interview.InterviewSession;
//import com.kkori.component.interview.QuestionForm;
//import com.kkori.component.interview.QuestionType;
//import com.kkori.entity.*;
//import com.kkori.exception.audio.AudioProcessingException;
//import com.kkori.exception.interview.InterviewRoomException;
//import com.kkori.exception.interview.InterviewSessionException;
//import com.kkori.exception.interview.TailQuestionException;
//import com.kkori.exception.user.UserException;
//import com.kkori.repository.*;
//import com.kkori.service.InterviewSessionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 면접 세션 관리 서비스 구현체
// * 면접 방 생성부터 완료까지의 전체 라이프사이클을 관리
// */
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class InterviewSessionServiceImpl implements InterviewSessionService {
//
//    private final InterviewRepository interviewRepository;
//    private final QuestionRepository questionRepository;
//    private final AnswerRepository answerRepository;
//    private final InterviewRecordRepository interviewRecordRepository;
//    private final UserRepository userRepository;
//    private final QuestionSetRepository questionSetRepository;
////    private final QuestionSetItemRepository questionSetItemRepository;
//
//    private final InterviewRoomManager roomManager;
//    private final Transcriber transcriber;
//    private final TailQuestionGenerator tailQuestionGenerator;
//
//    // ==================== 방 생성 및 참여 ====================
//
//    @Override
//    public String createSoloRoom(Long questionSetId, Long creatorId) {
//        List<QuestionForm> defaultQuestions = loadQuestionSet(questionSetId);
//        InterviewSession session = new InterviewSession(defaultQuestions);
//
//        return roomManager.createSoloRoom(questionSetId, creatorId, session);
//    }
//
//    @Override
//    public String createPairRoom(Long questionSetId, Long creatorId) {
//        List<QuestionForm> defaultQuestions = loadQuestionSet(questionSetId);
//        InterviewSession session = new InterviewSession(defaultQuestions);
//
//        return roomManager.createPairRoom(questionSetId, creatorId, session);
//    }
//
//    @Override
//    public void joinRoom(String roomId, Long userId) {
//        roomManager.joinRoom(roomId, userId);
//    }
//
//    // ==================== 면접 라이프사이클 ====================
//
//    @Override
//    @Transactional
//    public Long startInterview(String roomId, Long userId) {
//        // 1. 방 조회 및 권한 확인
//        InterviewRoom room = roomManager.getRoom(roomId);
//        validateStartPermission(room, userId);
//
//        // 2. 사용자 및 질문셋 조회
//        User interviewer = getUserById(room.getInterviewerId());
//        User interviewee = getUserById(room.getIntervieweeId());
//        QuestionSet questionSet = getQuestionSetById(room.getQuestionSetId());
//
//        // 3. Interview 엔티티 생성 및 저장
//        Interview interview = Interview.builder()
//                .interviewer(interviewer)
//                .interviewee(interviewee)
//                .usedQuestionSet(questionSet)
//                .roomId(roomId)
//                .build();
//
//        Interview savedInterview = interviewRepository.save(interview);
//
//        // 4. 메모리 방 상태 업데이트
//        roomManager.startInterview(roomId, savedInterview.getInterviewId());
//
//        return savedInterview.getInterviewId();
//    }
//
//    @Override
//    @Transactional
//    public void completeInterview(String roomId) {
//        // 1. 방 조회 (사용자 정보 보존을 위해 DB 저장 전에 조회)
//        InterviewRoom room = roomManager.getRoom(roomId);
//
//        if (!room.isStarted()) {
//            throw InterviewRoomException.interviewNotStarted();
//        }
//
//        // 2. Interview 조회 및 완료 처리
//        Interview interview = getInterviewById(room.getInterviewId());
//        interview.complete();
//
//        // 3. 질문-답변 데이터 DB 저장
//        saveInterviewData(interview, room.getSession());
//
//        // 4. 메모리에서 방 정리
//        roomManager.completeInterview(roomId);
//    }
//
//    @Override
//    @Transactional
//    public void exitRoom(String roomId, Long userId) {
//        InterviewRoom room = roomManager.getRoom(roomId);
//
//        // 면접 진행 중이면 DB 저장 후 처리
//        if (room.isStarted()) {
//            Interview interview = getInterviewById(room.getInterviewId());
//            interview.complete();
//
//            // 현재까지의 질문-답변 데이터 저장
//            saveInterviewData(interview, room.getSession());
//        }
//
//        // 메모리에서 사용자 제거 및 방 정리
//        roomManager.exitRoom(roomId, userId);
//    }
//
//    // ==================== 답변 처리 ====================
//
//    @Override
//    public String processAudioAnswer(String roomId, Long userId, String audioFilePath) {
//        // 권한 검증
//        InterviewRoom room = roomManager.getRoom(roomId);
//        validateAnswerPermission(room, userId);
//
//        try {
//            // STT 처리 (Transcriber 컴포넌트 사용)
//            String answerText = transcriber.transcribe(audioFilePath);
//
//            // 답변 처리
//            processAnswer(roomId, answerText);
//
//            return answerText;
//
//        } catch (Exception e) {
//            throw AudioProcessingException.audioTranscriptionFailed();
//        }
//    }
//
//    // ==================== 질문 관리 ====================
//
//    @Override
//    public QuestionForm getCurrentQuestion(String roomId) {
//        InterviewSession session = getSession(roomId);
//        return session.getCurrentQuestion();
//    }
//
//    @Override
//    public List<QuestionForm> getNextQuestions(String roomId) {
//        return generateTailQuestions(roomId);
//    }
//
//    @Override
//    public List<QuestionForm> generateTailQuestions(String roomId) {
//        try {
//            InterviewSession session = getSession(roomId);
//
//            // TailQuestionGenerator를 사용하여 꼬리질문 생성
//            List<String> gptQuestions = tailQuestionGenerator.generateTailQuestions(session.getQuestionAnswer());
//
//            // 생성된 꼬리질문을 실제 세션에 적용
//            return session.getNextQuestions(gptQuestions);
//
//        } catch (Exception e) {
//            throw TailQuestionException.tailQuestionGenerationFailed();
//        }
//    }
//
//    @Override
//    public QuestionForm selectQuestion(String roomId, QuestionType type, int questionId, String questionText) {
//        InterviewSession session = getSession(roomId);
//        return session.selectQuestion(type, questionId, questionText);
//    }
//
//    @Override
//    public QuestionForm createCustomQuestion(String roomId, String questionText) {
//        InterviewSession session = getSession(roomId);
//        return session.createCustomQuestion(questionText);
//    }
//
//    // ==================== 역할 및 상태 관리 ====================
//
//    @Override
//    public void swapRoles(String roomId) {
//        roomManager.swapRoles(roomId);
//    }
//
//    @Override
//    public boolean canJoinRoom(String roomId) {
//        return roomManager.canJoinRoom(roomId);
//    }
//
//    @Override
//    public boolean canStartInterview(String roomId) {
//        return roomManager.canStartInterview(roomId);
//    }
//
//    // ==================== Private 헬퍼 메서드들 ====================
//
//    /**
//     * 답변 처리 공통 로직
//     */
//    private void processAnswer(String roomId, String answerText) {
//        InterviewSession session = getSession(roomId);
//        session.saveAnswer(answerText);
//    }
//
//    /**
//     * 질문-답변 데이터를 DB에 저장
//     */
//    private void saveInterviewData(Interview interview, InterviewSession session) {
//        Map<QuestionForm, String> questionAnswers = session.getQuestionAnswer();
//        int orderNum = 1;
//
//        for (Map.Entry<QuestionForm, String> entry : questionAnswers.entrySet()) {
//            QuestionForm questionForm = entry.getKey();
//            String answerText = entry.getValue();
//
//            // 1. 질문 타입별로 Question 엔티티 저장
//            Question question = saveQuestionByType(questionForm);
//
//            // 2. Answer 엔티티 저장
//            Answer answer = saveAnswer(question, interview.getInterviewee(), answerText);
//
//            // 3. InterviewRecord 저장 (면접-질문-답변 연결)
//            saveInterviewRecord(interview, question, answer, orderNum++);
//        }
//    }
//
//    /**
//     * 질문 타입별 Question 엔티티 저장
//     */
//    private Question saveQuestionByType(QuestionForm questionForm) {
//        return switch (questionForm.getQuestionType()) {
//            case DEFAULT -> findOrCreateDefaultQuestion(questionForm);
//            case CUSTOM -> saveCustomQuestion(questionForm);
//            case TAIL -> saveTailQuestion(questionForm);
//        };
//    }
//
//    /**
//     * 기본 질문 조회 또는 생성
//     */
//    private Question findOrCreateDefaultQuestion(QuestionForm questionForm) {
//        return questionRepository.findById(Long.valueOf(questionForm.getQuestionId()))
//                .orElseThrow(() -> InterviewSessionException.defaultQuestionNotFound());
//    }
//
//    /**
//     * 커스텀 질문 저장
//     */
//    private Question saveCustomQuestion(QuestionForm questionForm) {
//        Question question = Question.customBuilder()
//                .content(questionForm.getQuestionText())
//                .build();
//        return questionRepository.save(question);
//    }
//
//    /**
//     * 꼬리 질문 저장
//     */
//    private Question saveTailQuestion(QuestionForm questionForm) {
//        Question parentQuestion = findParentQuestion(questionForm);
//
//        Question question = Question.tailBuilder()
//                .content(questionForm.getQuestionText())
//                .parent(parentQuestion)
//                .build();
//        return questionRepository.save(question);
//    }
//
//    /**
//     * 꼬리질문의 부모 질문 찾기
//     */
//    private Question findParentQuestion(QuestionForm questionForm) {
//        int parentId = questionForm.getParentQuestionId();
//
//        return questionRepository.findById(Long.valueOf(parentId))
//                .orElseThrow(() -> InterviewSessionException.parentQuestionNotFound());
//    }
//
//    /**
//     * 답변 저장
//     */
//    private Answer saveAnswer(Question question, User user, String answerText) {
//        Answer answer = Answer.builder()
//                .question(question)
//                .user(user)
//                .content(answerText)
//                .build();
//        return answerRepository.save(answer);
//    }
//
//    /**
//     * 면접 기록 저장
//     */
//    private void saveInterviewRecord(Interview interview, Question question, Answer answer, int orderNum) {
//        InterviewRecord record = InterviewRecord.builder()
//                .interview(interview)
//                .question(question)
//                .answer(answer)
//                .orderNum(orderNum)
//                .build();
//        interviewRecordRepository.save(record);
//    }
//
//    /**
//     * 질문셋 로딩
//     */
//    private List<QuestionForm> loadQuestionSet(Long questionSetId) {
//        List<QuestionSetItem> items = questionSetItemRepository.findByQuestionSetSetIdOrderBySortOrderAsc(questionSetId);
//
//        return items.stream()
//                .map(item -> new QuestionForm(
//                        QuestionType.DEFAULT,
//                        item.getQuestion().getQuestionId().intValue(),
//                        item.getQuestion().getContent()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 세션 조회
//     */
//    private InterviewSession getSession(String roomId) {
//        return roomManager.getSession(roomId);
//    }
//
//    // ==================== 검증 메서드들 ====================
//
//    /**
//     * 면접 시작 권한 검증
//     */
//    private void validateStartPermission(InterviewRoom room, Long userId) {
//        if (!room.getCreatorId().equals(userId)) {
//            throw InterviewSessionException.permissionDenied();
//        }
//    }
//
//    /**
//     * 답변 제출 권한 검증
//     */
//    private void validateAnswerPermission(InterviewRoom room, Long userId) {
//        if (!userId.equals(room.getIntervieweeId())) {
//            throw InterviewSessionException.onlyIntervieweeCanSubmitAnswer();
//        }
//
//        if (!room.isStarted()) {
//            throw InterviewRoomException.interviewNotStarted();
//        }
//    }
//
//    // ==================== 엔티티 조회 메서드들 ====================
//
//    /**
//     * 사용자 조회
//     */
//    private User getUserById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> UserException.userNotFound());
//    }
//
//    /**
//     * 질문셋 조회
//     */
//    private QuestionSet getQuestionSetById(Long questionSetId) {
//        return questionSetRepository.findById(questionSetId)
//                .orElseThrow(() -> InterviewSessionException.questionSetNotFound());
//    }
//
//    /**
//     * 면접 조회
//     */
//    private Interview getInterviewById(Long interviewId) {
//        return interviewRepository.findById(interviewId)
//                .orElseThrow(() -> InterviewSessionException.interviewNotFound());
//    }
//}