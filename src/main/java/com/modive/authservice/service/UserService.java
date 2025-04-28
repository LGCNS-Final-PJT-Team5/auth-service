package com.modive.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.modive.authservice.response.KakaoUserResponse;
import com.modive.authservice.domain.User;
import com.modive.authservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long createUser(final KakaoUserResponse userResponse) {
        User user = User.of(
                userResponse.kakaoAccount().profile().nickname(),
                userResponse.kakaoAccount().profile().profileImageUrl(),
                userResponse.kakaoAccount().profile().accountEmail(),
                String.valueOf(userResponse.id())
        );
        return userRepository.save(user).getId();
    }

}
