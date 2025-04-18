package com.example.WITHUS.config;

import com.example.WITHUS.security.JwtAuthenticationFilter;
import com.example.WITHUS.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    // 1. 비밀번호 암호화 방식 (회원가입 시에도 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. AuthenticationManager 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 3. 보안 필터 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/login",
                                "/api/signup",
                                "/api/user/me",
                                "/api/user/images/**",
                                "/profile_images/**",
                                "/api/community",                 // GET 요청 (게시글 전체 조회)
                                "/api/community/upload",          // 🔥 POST 요청 (게시글 등록)
                                "/api/user/update",
                                "/api/community/images/**",        // 🔥 이미지 파일 조회
                                "/api/community/**",
                                "/api/community/my",
                                "/api/community/user/**",
                                "/api/comment/**",
                                "/api/user/change-password",
                                "/api/delete/**",
                                "/api/user/**",
                                "/api/follow/add",
                                "/api/follow/delete",
                                "/api/follow/**",
                                "/api/block/**",
                                "/api/croom/exists",
                                "/api/croom/**",
                                "/ws/**",
                                "/api/recommend",
                                "/api/contest/views/**",
                                "/api/contest/view/**",
                                "/api/team/**",
                                "/api/upload",
                                "/uploaded/**",
                                "/api/download/**",
                                "api/todo/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(customUserDetailsService);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}