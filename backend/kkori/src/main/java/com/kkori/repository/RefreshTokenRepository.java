package com.kkori.repository;

import com.kkori.entity.RefreshToken;
import com.kkori.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findAllByUser(User user);
}