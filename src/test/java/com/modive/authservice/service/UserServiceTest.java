package com.modive.authservice.service;

import com.modive.authservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return true when nickname already exists")
    void testNicknameDuplicateCheck_NicknameExists() {
        // Arrange
        String existingNickname = "username123";
        when(userRepository.existsByNickname(existingNickname)).thenReturn(true);

        // Act
        boolean result = userService.nicknameDuplicateCheck(existingNickname);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when nickname does not exist")
    void testNicknameDuplicateCheck_NicknameDoesNotExist() {
        // Arrange
        String nonExistingNickname = "newUser456";
        when(userRepository.existsByNickname(nonExistingNickname)).thenReturn(false);

        // Act
        boolean result = userService.nicknameDuplicateCheck(nonExistingNickname);

        // Assert
        assertFalse(result);
    }
}