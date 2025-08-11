package com.kkori.controller;

import com.kkori.dto.interview.response.InterviewRecordDetailResponse;
import com.kkori.dto.interview.response.InterviewRecordListResponse;
import com.kkori.dto.interview.response.QuestionAnswerRecord;
import com.kkori.entity.QuestionType;
import com.kkori.security.CustomUserDetails;
import com.kkori.service.InterviewRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewRecordController.class)
class InterviewRecordControllerTest {

    private static final Long TEST_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InterviewRecordService interviewRecordService;

    @Test
    @DisplayName("해피케이스: 면접 기록 목록 조회 성공")
    void getInterviewRecords_Success() throws Exception {
        // Given
        setupMockUser();
        
        InterviewRecordListResponse record1 = InterviewRecordListResponse.builder()
                .interviewId(1L)
                .roomId("ROOM123")
                .interviewerNickname("면접관")
                .intervieweeNickname("면접자")
                .questionSetTitle("Spring Boot 면접")
                .totalQuestionCount(5)
                .completedAt(LocalDateTime.now())
                .userRole("INTERVIEWER")
                .build();
        
        InterviewRecordListResponse record2 = InterviewRecordListResponse.builder()
                .interviewId(2L)
                .roomId("ROOM456")
                .interviewerNickname("면접관2")
                .intervieweeNickname("면접자2")
                .questionSetTitle("Java 기초 면접")
                .totalQuestionCount(3)
                .completedAt(LocalDateTime.now())
                .userRole("INTERVIEWEE")
                .build();
        
        Page<InterviewRecordListResponse> recordsPage = new PageImpl<>(Arrays.asList(record1, record2));
        
        given(interviewRecordService.getInterviewRecords(eq(TEST_USER_ID), eq(0), eq(10), isNull()))
                .willReturn(recordsPage);
        
        // When & Then
        mockMvc.perform(get("/api/interview-records")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("면접 기록 목록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].interviewId").value(1))
                .andExpect(jsonPath("$.data.content[0].roomId").value("ROOM123"))
                .andExpect(jsonPath("$.data.content[0].interviewerNickname").value("면접관"))
                .andExpect(jsonPath("$.data.content[0].intervieweeNickname").value("면접자"))
                .andExpect(jsonPath("$.data.content[0].questionSetTitle").value("Spring Boot 면접"))
                .andExpect(jsonPath("$.data.content[0].totalQuestionCount").value(5))
                .andExpect(jsonPath("$.data.content[0].userRole").value("INTERVIEWER"))
                .andExpect(jsonPath("$.data.content[1].interviewId").value(2))
                .andExpect(jsonPath("$.data.content[1].userRole").value("INTERVIEWEE"));
        
        verify(interviewRecordService).getInterviewRecords(TEST_USER_ID, 0, 10, null);
    }
    
    @Test
    @DisplayName("해피케이스: 역할 필터로 면접 기록 목록 조회 성공")
    void getInterviewRecords_WithRoleFilter_Success() throws Exception {
        // Given
        setupMockUser();
        
        InterviewRecordListResponse record = InterviewRecordListResponse.builder()
                .interviewId(1L)
                .roomId("ROOM123")
                .interviewerNickname("면접관")
                .intervieweeNickname("면접자")
                .questionSetTitle("Spring Boot 면접")
                .totalQuestionCount(5)
                .completedAt(LocalDateTime.now())
                .userRole("INTERVIEWER")
                .build();
        
        Page<InterviewRecordListResponse> recordsPage = new PageImpl<>(Arrays.asList(record));
        
        given(interviewRecordService.getInterviewRecords(eq(TEST_USER_ID), eq(0), eq(10), eq("interviewer")))
                .willReturn(recordsPage);
        
        // When & Then
        mockMvc.perform(get("/api/interview-records")
                        .param("page", "0")
                        .param("size", "10")
                        .param("role", "interviewer")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].userRole").value("INTERVIEWER"));
        
        verify(interviewRecordService).getInterviewRecords(TEST_USER_ID, 0, 10, "interviewer");
    }
    
    @Test
    @DisplayName("해피케이스: 면접 상세 기록 조회 성공")
    void getInterviewRecordDetail_Success() throws Exception {
        // Given
        setupMockUser();
        Long interviewId = 1L;
        
        QuestionAnswerRecord qa1 = QuestionAnswerRecord.builder()
                .recordId(1L)
                .orderNum(1)
                .questionId(1L)
                .questionContent("Spring Boot란 무엇인가요?")
                .questionType(QuestionType.DEFAULT)
                .expectedAnswer("Spring Boot는...")
                .parentQuestionId(null)
                .parentQuestionContent(null)
                .answerId(1L)
                .answerContent("Spring Boot는 스프링 프레임워크를 기반으로 한...")
                .answeredAt(LocalDateTime.now())
                .build();
        
        QuestionAnswerRecord qa2 = QuestionAnswerRecord.builder()
                .recordId(2L)
                .orderNum(2)
                .questionId(2L)
                .questionContent("좀 더 자세히 설명해주세요")
                .questionType(QuestionType.TAIL)
                .expectedAnswer(null)
                .parentQuestionId(1L)
                .parentQuestionContent("Spring Boot란 무엇인가요?")
                .answerId(2L)
                .answerContent("Spring Boot의 핵심 기능으로는...")
                .answeredAt(LocalDateTime.now())
                .build();
        
        InterviewRecordDetailResponse detailResponse = InterviewRecordDetailResponse.builder()
                .interviewId(1L)
                .roomId("ROOM123")
                .interviewerNickname("면접관")
                .intervieweeNickname("면접자")
                .questionSetTitle("Spring Boot 면접")
                .completedAt(LocalDateTime.now())
                .userRole("INTERVIEWER")
                .questionAnswers(Arrays.asList(qa1, qa2))
                .build();
        
        given(interviewRecordService.getInterviewRecordDetail(eq(TEST_USER_ID), eq(interviewId)))
                .willReturn(detailResponse);
        
        // When & Then
        mockMvc.perform(get("/api/interview-records/{interviewId}", interviewId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("면접 상세 기록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.interviewId").value(1))
                .andExpect(jsonPath("$.data.roomId").value("ROOM123"))
                .andExpect(jsonPath("$.data.userRole").value("INTERVIEWER"))
                .andExpect(jsonPath("$.data.questionAnswers").isArray())
                .andExpect(jsonPath("$.data.questionAnswers[0].orderNum").value(1))
                .andExpect(jsonPath("$.data.questionAnswers[0].questionType").value("DEFAULT"))
                .andExpect(jsonPath("$.data.questionAnswers[0].parentQuestionId").isEmpty())
                .andExpect(jsonPath("$.data.questionAnswers[1].orderNum").value(2))
                .andExpect(jsonPath("$.data.questionAnswers[1].questionType").value("TAIL"))
                .andExpect(jsonPath("$.data.questionAnswers[1].parentQuestionId").value(1))
                .andExpect(jsonPath("$.data.questionAnswers[1].parentQuestionContent").value("Spring Boot란 무엇인가요?"));
        
        verify(interviewRecordService).getInterviewRecordDetail(TEST_USER_ID, interviewId);
    }
    
    @Test
    @DisplayName("해피케이스: 기본 페이징 파라미터로 면접 기록 목록 조회")
    void getInterviewRecords_DefaultPaging_Success() throws Exception {
        // Given
        setupMockUser();
        
        Page<InterviewRecordListResponse> emptyPage = new PageImpl<>(List.of());
        
        given(interviewRecordService.getInterviewRecords(eq(TEST_USER_ID), eq(0), eq(10), isNull()))
                .willReturn(emptyPage);
        
        // When & Then
        mockMvc.perform(get("/api/interview-records")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        // 기본값 확인: page=0, size=10, role=null
        verify(interviewRecordService).getInterviewRecords(TEST_USER_ID, 0, 10, null);
    }
    
    // ============== 헬퍼 메서드 ==============
    
    private void setupMockUser() {
        CustomUserDetails principal = new CustomUserDetails(
                TEST_USER_ID,
                "testUserSub",
                false,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}