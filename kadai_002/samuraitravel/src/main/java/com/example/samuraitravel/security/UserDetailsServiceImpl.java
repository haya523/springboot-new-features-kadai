package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;    

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;        
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) ユーザー取得（nullなら例外）
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした: " + email);
        }

        // 2) 権限付与：DBに入っている文字列をそのまま使う（例: ROLE_GENERAL, ROLE_ADMIN）
        String roleName = (user.getRole() != null) ? user.getRole().getName() : null;

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (roleName != null && !roleName.isBlank()) {
            authorities.add(new SimpleGrantedAuthority(roleName));
        }

        // 3) UserDetails を返す（パスワードはハッシュ、enabled はエンティティの値）
        return new UserDetailsImpl(user, authorities);
    }
}
