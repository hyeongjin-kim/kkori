package com.kkori.security;

import com.kkori.exception.user.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 보안 컨텍스트 검증 유틸리티
 * - JWT 토큰 유효성 검증
 * - 사용자 권한 검증
 * - 보안 로깅
 */
@Slf4j
@Component
public class SecurityContextValidator {

    /**
     * 현재 로그인한 사용자 ID 조회 및 검증
     */
    public Long validateAndGetCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt");
            throw UserException.unauthorized();
        }

        if (authentication.getPrincipal() == null) {
            log.warn("Authentication principal is null");
            throw UserException.unauthorized();
        }

        try {
            Long userId = Long.valueOf(authentication.getName());
            log.debug("Validated user access: userId={}", userId);
            return userId;
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format in authentication: {}", authentication.getName());
            throw UserException.invalidUserFormat();
        }
    }

    /**
     * 사용자 소유권 검증
     */
    public void validateUserOwnership(Long resourceOwnerId, Long currentUserId) {
        if (!resourceOwnerId.equals(currentUserId)) {
            log.warn("Access denied: user {} attempted to access resource owned by {}", 
                     currentUserId, resourceOwnerId);
            throw UserException.accessDenied();
        }
    }

    /**
     * 관리자 권한 검증
     */
    public void validateAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!hasAdminRole) {
            log.warn("Admin access denied for user: {}", authentication.getName());
            throw UserException.adminRequired();
        }
    }

    /**
     * 보안 감사 로그 기록
     */
    public void auditSecurityEvent(String action, Long userId, String resourceType, Long resourceId) {
        log.info("SECURITY_AUDIT: action={}, userId={}, resourceType={}, resourceId={}", 
                 action, userId, resourceType, resourceId);
    }
}