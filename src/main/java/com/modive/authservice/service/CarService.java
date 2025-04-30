package com.modive.authservice.service;

import com.modive.authservice.domain.Car;
import com.modive.authservice.domain.User;
import com.modive.authservice.repository.CarRepository;
import com.modive.authservice.repository.UserRepository;
import com.modive.authservice.response.CarListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarListResponse getCarList(Long userId) {
        List<Car> usersCars = carRepository.findByUserUserId(userId);
        List<String> usersCarNumbers= usersCars.stream()
                .map(Car::getNumber)
                .filter(Objects::nonNull) // null 닉네임 필터링
                .toList();
        return CarListResponse.of(usersCarNumbers);
    }

    public void addCar(Long userId, String number) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Car car = new Car();
        car.setUser(user);
        car.setNumber(number);
        carRepository.save(car);
    }

    public void deleteCar(String number) {
        Car car = carRepository.findByNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("차량을 찾을 수 없습니다."));
        carRepository.delete(car);
    }
}
