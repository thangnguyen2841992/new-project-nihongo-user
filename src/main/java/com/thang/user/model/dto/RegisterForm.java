package com.thang.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterForm {
    private String username;

    private String password;

    private String confirmPassword;

    private String dateOfBirth;

    private String email;

    private String phoneNumber;

    private int gender;

    private String address;

    private String firstName;

    private String lastName;




}
