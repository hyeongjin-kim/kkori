package com.kkori.service;

import com.kkori.component.interview.QuestionForm;
import com.kkori.component.interview.QuestionType;

import java.util.List;
import java.util.Map;

public interface InterviewSessionService {

    // ==================== 방 생성 및 참여 ====================

    /**
     * 혼자 연습하기 방 생성
     */
    String createSoloRoom(Long questionSetId, Long creatorId);

    /**
     * 함께 연습하기 방 생성
     */
    String createPairRoom(Long questionSetId, Long creatorId);

    /**
     * 방 참여 (함께 연습하기만 가능)
     */
    void joinRoom(String roomId, Long userId);

    // ==================== 면접 ====================

    /**
     * 면접 시작 - DB에 Interview 생성 후 방 상태 업데이트
     */
    Long startInterview(String roomId, Long userId);

    /**
     * 면접 완료 - 메모리 데이터를 DB에 저장 후 방 정리
     */
    void completeInterview(String roomId);

    /**
     * 방 나가기 - 면접 진행 중이면 DB 저장 후 처리
     */
    void exitRoom(String roomId, Long userId);

    // ==================== 답변 처리 ====================

    /**
     * 음성 파일로 답변 처리 (STT 변환 포함)
     */
    String processAudioAnswer(String roomId, Long userId, String audioBase64);

    // ==================== 질문 관리 ====================

    /**
     * 현재 질문 조회
     */
    QuestionForm getCurrentQuestion(String roomId);

    /**
     * 다음 질문들 조회 (답변 후 - 꼬리질문 + 기본질문)
     */
    List<QuestionForm> getNextQuestions(String roomId);

    /**
     * 꼬리질문 생성 (GPT 기반)
     */
    List<QuestionForm> generateTailQuestions(String roomId);

    /**
     * 질문 선택
     */
    QuestionForm selectQuestion(String roomId, QuestionType type, int questionId, String questionText);

    /**
     * 커스텀 질문 생성
     */
    QuestionForm createCustomQuestion(String roomId, String audioBase64);

    // ==================== 역할 및 상태 관리 ====================

    /**
     * 역할 변경 (함께 연습하기에서만 가능)
     */
    void swapRoles(String roomId);


    /**
     * 방 참여 가능 여부 확인
     */
    boolean canJoinRoom(String roomId);

    /**
     * 면접 시작 가능 여부 확인
     */
    boolean canStartInterview(String roomId);
}