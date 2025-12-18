package com.thang.user.repository;

import com.thang.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);
    boolean existsByPhoneNumber (String phoneNumber);
    Optional<User> findByUsername(String username);
}
