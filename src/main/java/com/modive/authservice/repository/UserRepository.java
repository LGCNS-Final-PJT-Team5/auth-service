package com.modive.authservice.repository;

import com.modive.authservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 소셜 ID와 소셜 타입으로 사용자 찾기
    Optional<User> findBySocialIdAndSocialType(String socialId, String socialType);

    // 이메일이 존재하는지 확인
    boolean existsByEmail(String email);
    
    // 닉네임이 존재하는지 확인
    boolean existsByNickname(String nickname);

    User findByUserId(String userId);

    User findByNickname(String nickname);

    List<User> findAll();

    String deleteUserByUserId(String userId);
}