package com.kkori.service;

import com.kkori.entity.User;
import com.kkori.exception.user.UserException;
import com.kkori.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    @Override
    public ResponseEntity<Void> logout(Long userId, HttpServletResponse response) {

        Cookie clearRefresh = new Cookie("refreshToken", "");
        clearRefresh.setHttpOnly(true);
        clearRefresh.setSecure(true);
        clearRefresh.setPath("/");
        clearRefresh.setMaxAge(0);
        response.addCookie(clearRefresh);

        Cookie clearAccess = new Cookie("accessToken", "");
        clearAccess.setHttpOnly(true);
        clearAccess.setSecure(true);
        clearAccess.setPath("/");
        clearAccess.setMaxAge(0);
        response.addCookie(clearAccess);

        return ResponseEntity.ok().build();
    }
}