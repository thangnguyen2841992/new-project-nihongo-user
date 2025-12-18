package com.thang.user.service.user;

import com.thang.user.model.dto.RegisterForm;
import com.thang.user.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Optional;

public interface IUserService extends UserDetailsService {
    Object registerUser(RegisterForm registerForm) throws IOException;
    Optional<User> findByUsername(String username);
}
