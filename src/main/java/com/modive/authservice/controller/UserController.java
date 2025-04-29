package com.modive.authservice.controller;

import com.modive.authservice.domain.User;
import com.modive.authservice.dto.UpdateAlarmRequest;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.response.*;
import com.modive.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public SuccessResponse<UserResponse> myInfo() {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userService.getUser(1L)
        );
    }

    @GetMapping("/{userId}")
    public SuccessResponse<UserResponse> userInfoById(@PathVariable("userId") Long userId) {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userService.getUser(userId)
        );
    }

    @GetMapping
    public SuccessResponse<UserResponse> userInfoByNickname(@RequestParam("search") String search) {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userService.getUser(search)
        );
    }

    @DeleteMapping("/{userId}")
    public SuccessResponseNoData<String> deleteUser(@PathVariable("userId") Long userId) {
        return SuccessResponseNoData.of(
                userService.deleteUser(userId)
        );
    }

    @GetMapping("/list")
    public SuccessResponse<UserListResponse> list() {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userService.getAllUserNicknames()
        );
    }

    @GetMapping("/nickname")
    public SuccessResponse<Boolean> nicknameDuplicateCheck(@RequestParam("search") String nickname) {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userRepository.existsByNickname(nickname)
        );
    }

    @PatchMapping("/{userId}/alarm")
    public SuccessResponseNoData<String> updateUserAlarm(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateAlarmRequest alarm
    ) {
        userService.updateUserAlarm(userId, alarm.isAlarm());
        return SuccessResponseNoData.of(
            "사용자 알람 설정에 설공했습니다."
        );

    }

    @GetMapping("/test")
    public SuccessResponse<User> test() {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                userRepository.findByUserId(1L)
        );
    }

}
