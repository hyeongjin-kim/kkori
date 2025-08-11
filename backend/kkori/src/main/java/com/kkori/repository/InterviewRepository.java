package com.kkori.repository;

import com.kkori.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    
    /**
     * 사용자가 참여한(면접관 또는 면접자) 완료된 면접 목록을 최신순으로 조회
     */
    @Query("SELECT i FROM Interview i " +
           "WHERE (i.interviewer.userId = :userId OR i.interviewee.userId = :userId) " +
           "AND i.status = com.kkori.component.interview.RoomStatus.COMPLETED " +
           "ORDER BY i.completedAt DESC")
    Page<Interview> findByUserIdAndCompleted(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 사용자가 면접자로 참여한 완료된 면접 목록을 최신순으로 조회  
     */
    @Query("SELECT i FROM Interview i " +
           "WHERE i.interviewee.userId = :userId " +
           "AND i.status = com.kkori.component.interview.RoomStatus.COMPLETED " +
           "ORDER BY i.completedAt DESC")
    Page<Interview> findByIntervieweeIdAndCompleted(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 사용자가 면접관으로 참여한 완료된 면접 목록을 최신순으로 조회
     */
    @Query("SELECT i FROM Interview i " +
           "WHERE i.interviewer.userId = :userId " +
           "AND i.status = com.kkori.component.interview.RoomStatus.COMPLETED " +
           "ORDER BY i.completedAt DESC")
    Page<Interview> findByInterviewerIdAndCompleted(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 면접 상세 조회 시 InterviewRecord와 연관 엔티티들을 함께 페치
     */
    @Query("SELECT i FROM Interview i " +
           "LEFT JOIN FETCH i.interviewRecords ir " +
           "LEFT JOIN FETCH ir.question q " +
           "LEFT JOIN FETCH ir.answer a " +
           "LEFT JOIN FETCH q.parent " +
           "WHERE i.interviewId = :interviewId " +
           "AND (i.interviewer.userId = :userId OR i.interviewee.userId = :userId)")
    Optional<Interview> findByIdAndUserIdWithRecords(@Param("interviewId") Long interviewId, @Param("userId") Long userId);
}
