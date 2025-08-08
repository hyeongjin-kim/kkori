package com.kkori.service;

import com.kkori.entity.User;

public interface UserService {
    User findById(Long userId);
}
