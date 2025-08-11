package com.kkori.service;

import com.kkori.exception.interview.TailQuestionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 동시성 제어 서비스
 * - 중요한 비즈니스 로직의 동시성 제어
 * - 분산 락이 아닌 인메모리 락 (단일 인스턴스 환경)
 * - 면접 답변 제출 등 중요한 작업의 동시성 보장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConcurrencyControlService {

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    /**
     * 특정 리소스에 대한 락 획득
     */
    public void acquireLock(String resourceId) {
        ReentrantLock lock = lockMap.computeIfAbsent(resourceId, k -> new ReentrantLock());
        
        try {
            log.debug("Attempting to acquire lock for resource: {}", resourceId);
            boolean acquired = lock.tryLock(java.util.concurrent.TimeUnit.SECONDS.toMillis(10), 
                                           java.util.concurrent.TimeUnit.MILLISECONDS);
            
            if (!acquired) {
                log.warn("Failed to acquire lock for resource: {} within timeout", resourceId);
                throw TailQuestionException.interviewConcurrencyError();
            }
            
            log.debug("Lock acquired for resource: {}", resourceId);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for resource: {}", resourceId);
            throw TailQuestionException.interviewConcurrencyError();
        }
    }

    /**
     * 특정 리소스의 락 해제
     */
    public void releaseLock(String resourceId) {
        ReentrantLock lock = lockMap.get(resourceId);
        
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("Lock released for resource: {}", resourceId);
            
            // 사용하지 않는 락 정리 (메모리 누수 방지)
            if (lock.getQueueLength() == 0) {
                lockMap.remove(resourceId);
                log.debug("Removed unused lock for resource: {}", resourceId);
            }
        }
    }

    /**
     * 락을 사용한 안전한 작업 실행
     */
    public <T> T executeWithLock(String resourceId, java.util.function.Supplier<T> operation) {
        acquireLock(resourceId);
        
        try {
            return operation.get();
        } finally {
            releaseLock(resourceId);
        }
    }

    /**
     * 낙관적 락 실패 재시도 메커니즘
     */
    public <T> T executeWithOptimisticLockRetry(java.util.function.Supplier<T> operation, int maxRetries) {
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                return operation.get();
                
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                log.warn("Optimistic lock conflict detected (attempt {}/{}): {}", 
                         retryCount, maxRetries, e.getMessage());
                
                if (retryCount >= maxRetries) {
                    log.error("Max retry attempts reached for optimistic lock operation");
                    throw TailQuestionException.interviewConcurrencyError();
                }
                
                // 지수 백오프로 재시도 대기
                try {
                    long waitTime = (long) (Math.pow(2, retryCount) * 100); // 200ms, 400ms, 800ms...
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw TailQuestionException.interviewConcurrencyError();
                }
            }
        }
        
        throw TailQuestionException.interviewConcurrencyError();
    }

    /**
     * 현재 활성 락 수 조회 (모니터링용)
     */
    public int getActiveLockCount() {
        return lockMap.size();
    }

    /**
     * 특정 리소스의 락 상태 조회
     */
    public boolean isLocked(String resourceId) {
        ReentrantLock lock = lockMap.get(resourceId);
        return lock != null && lock.isLocked();
    }
}