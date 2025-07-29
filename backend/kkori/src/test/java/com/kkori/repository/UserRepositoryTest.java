package com.kkori.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kkori.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    private static final String DUPLICATE_SUB = "duplicate-sub";
    private static final String TEST_SUB = "test-sub";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("신규 사용자 저장 및 조회 테스트")
    void saveAndFindUserBySub() {
        User user = new User(TEST_SUB, "테스트유저");
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getUserId());

        Optional<User> found = userRepository.findBySubAndDeletedFalse(TEST_SUB);
        assertTrue(found.isPresent());
        assertEquals("테스트유저", found.get().getNickname());
    }

    @Test
    @DisplayName("중복 sub 저장 시 예외 발생 테스트")
    void duplicateSubShouldThrowException() {
        User user1 = new User(DUPLICATE_SUB, "유저1");
        userRepository.save(user1);

        User user2 = new User(DUPLICATE_SUB, "유저2");
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user2));
    }

    @Test
    @DisplayName("없는 사용자 조회 시 Optional.empty 반환 테스트")
    void findBySubAndDeletedFalseNotExist() {
        Optional<User> userOpt = userRepository.findBySubAndDeletedFalse("nonexistent");
        assertTrue(userOpt.isEmpty());
    }
    
}
