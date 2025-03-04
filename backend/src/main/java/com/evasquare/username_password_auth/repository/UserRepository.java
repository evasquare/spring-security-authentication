package com.evasquare.username_password_auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.evasquare.username_password_auth.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
