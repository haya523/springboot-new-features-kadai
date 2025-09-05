package com.example.samuraitravel.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.samuraitravel.entity.User;

public class UserDetailsImpl implements org.springframework.security.core.userdetails.UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // ログインIDはメールアドレス
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 画面表示用
    public String getDisplayName() {
        return user.getName();
    }

    // コントローラ等で元の User を使いたいときのため
    public User getUser() {
        return user;
    }

    // ここを修正：Role 名を権限に変換して返す
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 例：ROLE_GENERAL / ROLE_ADMIN といった文字列が入っている想定
        String roleName = user.getRole() != null ? user.getRole().getName() : "ROLE_GENERAL";
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return Boolean.TRUE.equals(user.getEnabled()); }
}
