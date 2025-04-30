package com.modive.authservice.controller;

import com.modive.authservice.dto.CarNumberRequest;
import com.modive.authservice.dto.AlarmRequest;
import com.modive.authservice.dto.NicknameRequest;
import com.modive.authservice.dto.RewardRequest;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.response.*;
import com.modive.authservice.service.CarService;
import com.modive.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final CarService carService;

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

    @PatchMapping("/nickname")
    public SuccessResponseNoData<String> updateNickname(
            @RequestBody NicknameRequest request
    ) {
        userService.updateNickname(1L, request.getNickname());
        return SuccessResponseNoData.of(
                "닉네임 변경에 성공했습니다."
        );
    }

    @PatchMapping("/{userId}/alarm")
    public SuccessResponseNoData<String> updateUserAlarm(
            @PathVariable("userId") Long userId,
            @RequestBody AlarmRequest alarm
    ) {
        userService.updateUserAlarm(userId, alarm.isAlarm());
        return SuccessResponseNoData.of(
            "사용자 알람 설정에 설공했습니다."
        );
    }

    @PostMapping("/{userId}/reward")
    public SuccessResponseNoData<String> updateUserReward(
            @PathVariable("userId") Long userId,
            @RequestBody RewardRequest request
    ) {
        userService.updateUserReward(userId, request.getReward());
        return SuccessResponseNoData.of(
            "사용자 리워드 설정에 설공했습니다."
        );
    }

    @GetMapping("/car")
    public SuccessResponse<CarListResponse> getCarList() {
        return SuccessResponse.of(
                SuccessMessage.USER_INFO_SUCCESS,
                carService.getCarList(1L)
        );
    }

    @PostMapping("/car")
    public SuccessResponseNoData<String> addCar(
            @RequestBody final CarNumberRequest request
    ) {
        carService.addCar(1L, request.getNumber());
        return SuccessResponseNoData.of(
                "차량 등록에 설공했습니다."
        );
    }

    @DeleteMapping("/car")
    public SuccessResponseNoData<String> deleteCar(
            @RequestBody final CarNumberRequest request
    ) {
        carService.deleteCar(request.getNumber());
        return SuccessResponseNoData.of(
                "차량 삭제에 설공했습니다."
        );
    }
}
