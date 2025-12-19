package com.thang.user.controller;

import com.thang.user.model.dto.RegisterForm;
import com.thang.user.model.entity.User;
import com.thang.user.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth-api")
public class AuthRestController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterForm registerForm) throws IOException {
        User newUser = (User) this.userService.registerUser(registerForm);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
