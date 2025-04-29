package com.modive.authservice.service;

import com.modive.authservice.response.UserListResponse;
import com.modive.authservice.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.modive.authservice.response.KakaoUserResponse;
import com.modive.authservice.domain.User;
import com.modive.authservice.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long createKakaoUser(final KakaoUserResponse userResponse) {
        User user = User.of(
                userResponse.kakaoAccount().profile().nickname(),
                userResponse.kakaoAccount().profile().accountEmail(),
                String.valueOf(userResponse.id()),
                "kakao"
        );
        return userRepository.save(user).getUserId();
    }

    public Optional<User> findKakaoUser(final Long socialId) {
        return userRepository.findBySocialIdAndSocialType(String.valueOf(socialId), "kakao");
    }

    public UserResponse getUser(final Long userId) {
        return UserResponse.of(userRepository.findByUserId(userId));
    }

    public UserResponse getUser(final String nickname) {
        return UserResponse.of(userRepository.findByNickname(nickname));
    }

    public UserListResponse getAllUserNicknames() {
        List<User> allUsers = userRepository.findAll();
        List<String> allUserNicknames= allUsers.stream()
                .map(User::getNickname)
                .filter(Objects::nonNull) // null 닉네임 필터링
                .toList();
        return UserListResponse.of(allUserNicknames);
    }

    @Transactional
    public String deleteUser(final Long userId) {
        Long isDelete = userRepository.deleteUserByUserId(userId);
        if (Objects.equals(isDelete, userId)) {
            return "유저 삭제에 성공했습니다.";
        }
        else {
            return "유저 삭제에 실패했습니다.";
        }
    }

    @Transactional
    public void updateUserAlarm(Long userId, boolean alarm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAlarm(alarm);
    }
}
