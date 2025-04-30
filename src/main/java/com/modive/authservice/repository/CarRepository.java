package com.modive.authservice.repository;

import com.modive.authservice.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByUserUserId(Long userId);

    Optional<Car> findByNumber(String number);
}
