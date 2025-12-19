package com.thang.user.service.user;

import com.thang.user.model.dto.RegisterForm;
import com.thang.user.model.entity.Role;
import com.thang.user.model.entity.User;
import com.thang.user.repository.IUserRepository;
import com.thang.user.service.role.IRoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    @PostConstruct
    public void init() throws IOException {
        List<Role> roles = this.roleService.getRoles();
        if (roles.isEmpty()) {
            this.roleService.addRole(new Role("ROLE_ADMIN"));
            this.roleService.addRole(new Role("ROLE_USER"));
        }
        Optional<User> userOptional = findByUsername("admin");
        if (userOptional.isEmpty()) {
            registerAdmin();
        }
    }


    @Value("${user.avatar.default}")
    private  String AVATAR_DEFAULT;

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

    @Override
    public void registerAdmin() throws IOException {
        User newUser = new User();
        newUser.setUsername("admin");
        newUser.setFirstName("The");
        newUser.setLastName("Boss");
        newUser.setFullName(newUser.getFirstName() + " " + newUser.getLastName());
        newUser.setPassword(new BCryptPasswordEncoder().encode("thuThuy@1"));
        newUser.setRoles(getRole("ROLE_ADMIN"));
        newUser.setActive(true);
        newUser.setCodeActive(createActiveCode());
        newUser.setDateCreated(new Date());
        newUser.setEmail("nguyenthiquy29tbdl@gmail.com");
        newUser.setPhoneNumber("0989712888");
        newUser.setDateOfBirth(new Date());
        newUser.setGender(0);
        newUser.setAvatar(AVATAR_DEFAULT);
        newUser.setBlocked(false);
        newUser.setAddress("Hà Nội");
        this.userRepository.save(newUser);
    }

    private String createActiveCode() {
        return UUID.randomUUID().toString();
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
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        user.setUsername(registerForm.getUsername());
        user.setEmail(registerForm.getEmail());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setPhoneNumber(registerForm.getPhoneNumber());
        user.setAddress(registerForm.getAddress());
        user.setCodeActive(createActiveCode());
        user.setActive(false);
        user.setGender(registerForm.getGender());
        user.setAvatar(AVATAR_DEFAULT);
        user.setRoles(getRole("ROLE_USER"));
        user.setBlocked(false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), rolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }

    private Set<Role> getRole(String roleName) {
        Set<Role> roles = new HashSet<>();
        Optional<Role> roleOptional = this.roleService.findByRoleName(roleName);
        roleOptional.ifPresent(roles::add);
        return roles;
    }
}
