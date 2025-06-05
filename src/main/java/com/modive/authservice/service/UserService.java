package com.modive.authservice.service;

import com.modive.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
