package com.kkori.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 메서드 실행 시간 측정 및 로깅 AOP
 * - 모든 Service 클래스의 public 메서드 성능 모니터링
 * - 트랜잭션 실행 시간 추적
 * - 에러 발생 시 상세 로깅
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.kkori.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // 민감한 정보 필터링
        String sanitizedArgs = sanitizeArgs(args);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        log.info("SERVICE_CALL_START: {}.{}() args={}", className, methodName, sanitizedArgs);
        
        try {
            Object result = joinPoint.proceed();
            
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            log.info("SERVICE_CALL_SUCCESS: {}.{}() execution_time={}ms", 
                     className, methodName, executionTime);
            
            // 성능 경고 (2초 이상)
            if (executionTime > 2000) {
                log.warn("PERFORMANCE_WARNING: {}.{}() took {}ms - consider optimization", 
                         className, methodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            log.error("SERVICE_CALL_ERROR: {}.{}() failed after {}ms - error: {}", 
                      className, methodName, executionTime, e.getMessage(), e);
            
            throw e;
        }
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logTransactionalMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.debug("TRANSACTION_START: {}.{}()", className, methodName);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            Object result = joinPoint.proceed();
            
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            log.debug("TRANSACTION_COMMIT: {}.{}() execution_time={}ms", 
                     className, methodName, executionTime);
            
            return result;
            
        } catch (Exception e) {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            log.error("TRANSACTION_ROLLBACK: {}.{}() failed after {}ms - error: {}", 
                      className, methodName, executionTime, e.getMessage());
            
            throw e;
        }
    }

    /**
     * 민감한 정보 필터링
     */
    private String sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String && ((String) arg).toLowerCase().contains("password")) {
                sb.append("***MASKED***");
            } else if (arg instanceof String && ((String) arg).length() > 100) {
                // 긴 문자열은 축약
                sb.append("\"").append(((String) arg).substring(0, 100)).append("...\"");
            } else {
                sb.append(arg.getClass().getSimpleName()).append("@").append(Integer.toHexString(arg.hashCode()));
            }
        }
        sb.append("]");
        
        return sb.toString();
    }
}