package com.modive.authservice.repository;

import com.modive.authservice.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(String id);

    boolean existsById(String id);
}
