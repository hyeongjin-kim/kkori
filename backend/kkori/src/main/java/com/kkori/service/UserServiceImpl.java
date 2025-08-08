package com.kkori.service;

import com.kkori.entity.User;
import com.kkori.exception.user.UserException;
import com.kkori.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }
}