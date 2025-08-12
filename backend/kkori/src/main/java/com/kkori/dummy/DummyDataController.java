package com.kkori.dummy;

import com.kkori.annotation.LoginUser;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DummyDataController {

    private final DummyDataService dummyDataService;

    @PostMapping("/create-dummy-data")
    public ResponseEntity<Map<String, String>> createDummyData() {
        try {
            dummyDataService.createDummyData();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "전체 더미 데이터(User, QuestionSet, InterviewRecord 포함)가 성공적으로 생성되었습니다.");
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "더미 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/create-interview-records")
    public ResponseEntity<Map<String, String>> createInterviewRecordsOnly() {
        try {
            // InterviewRecord만 별도로 생성하는 메서드가 필요하면 DummyDataService에 추가
            dummyDataService.createDummyData();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "InterviewRecord 더미 데이터(완료된 면접 3개, 진행중 면접 1개)가 성공적으로 생성되었습니다.");
            response.put("details", "DEFAULT, CUSTOM, TAIL 질문들이 논리적 순서로 생성됨");
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "InterviewRecord 더미 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/create-my-interview-records")
    public ResponseEntity<Map<String, String>> createMyInterviewRecords(@LoginUser Long userId) {
        try {
            dummyDataService.createInterviewRecordsForUser(userId);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "로그인한 사용자의 InterviewRecord 더미 데이터가 성공적으로 생성되었습니다.");
            response.put("userId", userId.toString());
            response.put("details", "완료된 면접 3개(각 10개 질답), 진행중 면접 1개(7개 질답)");
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "InterviewRecord 더미 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
