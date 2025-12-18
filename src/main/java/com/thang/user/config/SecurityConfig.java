package com.thang.user.config;

import com.thang.user.model.entity.Role;
import com.thang.user.service.jwt.JwtFilter;
import com.thang.user.service.role.RoleServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final RoleServiceImpl roleService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(RoleServiceImpl roleService, JwtFilter jwtFilter) {
        this.roleService = roleService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {  // Mã hóa password
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @PostConstruct
    public void init() throws IOException {
        List<Role> roles = this.roleService.getRoles();
        if (roles.isEmpty()) {
            this.roleService.addRole(new Role("ROLE_ADMIN"));
            this.roleService.addRole(new Role("ROLE_USER"));
        }
//        Optional<User> userOptional = this.authService.findByUsername("admin");
//        if (userOptional.isEmpty()) {
//            this.authService.registerAdmin();
//        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                config -> config
                        .requestMatchers(HttpMethod.GET, EndPoint.PUBLIC_API).permitAll()
                        .requestMatchers(HttpMethod.POST, EndPoint.PUBLIC_API).permitAll()
                        .requestMatchers(HttpMethod.POST, EndPoint.ADMIN_API).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, EndPoint.ADMIN_API).hasRole("ADMIN")
        );
        http.cors(cors -> {
            cors.configurationSource(request -> {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.addAllowedOrigin(EndPoint.FRONT_END_HOST);
                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                corsConfig.addAllowedHeader("*");
                return corsConfig;
            });
        });
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
