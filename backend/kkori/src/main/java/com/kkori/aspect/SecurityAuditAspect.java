package com.kkori.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 보안 감사 로깅 AOP
 * - 중요한 비즈니스 작업에 대한 감사 로그
 * - 사용자 행위 추적
 * - 보안 이벤트 모니터링
 */
@Slf4j
@Aspect
@Component
public class SecurityAuditAspect {

    @Before("execution(* com.kkori.service.*.create*(..))")
    public void auditCreateOperations(JoinPoint joinPoint) {
        auditOperation("CREATE", joinPoint);
    }

    @Before("execution(* com.kkori.service.*.update*(..))")
    public void auditUpdateOperations(JoinPoint joinPoint) {
        auditOperation("UPDATE", joinPoint);
    }

    @Before("execution(* com.kkori.service.*.delete*(..))")
    public void auditDeleteOperations(JoinPoint joinPoint) {
        auditOperation("DELETE", joinPoint);
    }

    @Before("execution(* com.kkori.service.InterviewTailQuestionService.submitTailQuestionAnswer(..))")
    public void auditAnswerSubmission(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long tailQuestionId = args.length > 0 ? (Long) args[0] : null;
        
        String userId = getCurrentUserId();
        String ipAddress = getClientIpAddress();
        
        log.info("SECURITY_AUDIT: action=ANSWER_SUBMIT, userId={}, tailQuestionId={}, ip={}, method={}", 
                 userId, tailQuestionId, ipAddress, joinPoint.getSignature().getName());
    }

    @AfterReturning("execution(* com.kkori.service.QuestionSetService.getQuestionSetDetail(..))")
    public void auditDataAccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = args.length > 0 ? (Long) args[0] : null;
        Long questionSetId = args.length > 1 ? (Long) args[1] : null;
        
        String ipAddress = getClientIpAddress();
        
        log.info("SECURITY_AUDIT: action=DATA_ACCESS, userId={}, resourceType=QUESTION_SET, resourceId={}, ip={}", 
                 userId, questionSetId, ipAddress);
    }

    @AfterThrowing(pointcut = "execution(* com.kkori.service..*(..))", throwing = "exception")
    public void auditSecurityExceptions(JoinPoint joinPoint, Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        
        // 보안 관련 예외만 감사 로그
        if (isSecurityRelatedexception(exceptionType)) {
            String userId = getCurrentUserId();
            String ipAddress = getClientIpAddress();
            String method = joinPoint.getSignature().getName();
            
            log.warn("SECURITY_AUDIT: action=SECURITY_VIOLATION, userId={}, method={}, exception={}, ip={}, message={}", 
                     userId, method, exceptionType, ipAddress, exception.getMessage());
        }
    }

    private void auditOperation(String operation, JoinPoint joinPoint) {
        String userId = getCurrentUserId();
        String ipAddress = getClientIpAddress();
        String method = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.info("SECURITY_AUDIT: action={}, userId={}, service={}, method={}, ip={}", 
                 operation, userId, className, method, ipAddress);
    }

    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID: {}", e.getMessage());
        }
        return "anonymous";
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // X-Forwarded-For 헤더 확인 (프록시/로드밸런서 고려)
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                
                // X-Real-IP 헤더 확인
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Failed to get client IP: {}", e.getMessage());
        }
        return "unknown";
    }

    private boolean isSecurityRelatedexception(String exceptionType) {
        return exceptionType.contains("Access") || 
               exceptionType.contains("Permission") || 
               exceptionType.contains("Auth") ||
               exceptionType.contains("Unauthorized") ||
               exceptionType.contains("Forbidden");
    }
}