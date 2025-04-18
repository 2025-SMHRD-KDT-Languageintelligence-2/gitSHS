package com.example.WITHUS.security;

import com.example.WITHUS.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    // 사용자 ID
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    // 권한 (없다면 빈 리스트 반환)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명(비밀번호) 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가적으로 User 정보가 필요할 경우 getUser로 꺼내 쓸 수 있음
    public User getUser() {
        return user;
    }
}