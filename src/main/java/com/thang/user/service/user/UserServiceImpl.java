package com.thang.user.service.user;

import com.thang.user.model.dto.RegisterForm;
import com.thang.user.model.entity.Role;
import com.thang.user.model.entity.User;
import com.thang.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static boolean isValidPassword(String password) {
        // Regex pattern
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=.{8,}).*";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }


    @Override
    public Object registerUser(RegisterForm registerForm) throws IOException {
        List<String> errorMessages = new ArrayList<>();

        if (userRepository.existsByUsername(registerForm.getUsername())) {
            errorMessages.add("Username is existed!");
        }
        if (userRepository.existsByEmail(registerForm.getEmail())) {
            errorMessages.add("Email is existed!");
        }
        if (userRepository.existsByPhoneNumber(registerForm.getPhoneNumber())) {
            errorMessages.add("Phone number is existed!");
        }
        if (!isValidPassword(registerForm.getPassword())) {
            errorMessages.add("Password is not valid!");
        }
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            errorMessages.add("Password is not matched!");
        }

        if (errorMessages.isEmpty()) {
            return createUser(registerForm);
        } else {
            return String.join(" ", errorMessages);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    private User createUser(RegisterForm registerForm) {
        User newUser = new User();
        setUserDetails(newUser, registerForm);
        newUser.setDateCreated(new Date());
        return userRepository.save(newUser);
    }

    private void setUserDetails(User user, RegisterForm registerForm) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date birthDate = formatter.parse(registerForm.getDateOfBirth());
            user.setDateOfBirth(birthDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid birthday");
        }

        user.setFirstName(registerForm.getFirstName());
        user.setLastName(registerForm.getLastName());
        user.setUsername(registerForm.getUsername());
        user.setEmail(registerForm.getEmail());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setPhoneNumber(registerForm.getPhoneNumber());
        user.setAddress(registerForm.getAddress());
//        user.setCodeActive(createActiveCode());
        user.setActive(false);
        user.setGender(registerForm.getGender());
//        user.setAvatar(AVATAR_DEFAULT);
//        user.setRoles(getRole("ROLE_USER"));
//        user.setBlock(false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), rolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }
}
