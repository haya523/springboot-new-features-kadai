package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/logout", "/signup",
                                 "/css/**", "/images/**", "/js/**",
                                 "/storage/**",
                                 "/houses/**",            // 民宿詳細は未ログインでも閲覧可
                                 "/reviews/house/**"      // レビュー一覧も閲覧可
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login").permitAll()
                .usernameParameter("email")
                .passwordParameter("password")
                // ★ 成功時ハンドラ：?redirect= を最優先 → 保存済みリクエスト → "/" の順で遷移
                .successHandler((request, response, authentication) -> {
                    String target = request.getParameter("redirect");
                    if (target != null && !target.isBlank()) {
                        response.sendRedirect(target);
                        return;
                    }
                    SavedRequest saved =
                        new HttpSessionRequestCache().getRequest(request, response);
                    if (saved != null) {
                        response.sendRedirect(saved.getRedirectUrl());
                        return;
                    }
                    response.sendRedirect("/");
                })
                .failureUrl("/login?error")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?loggedOut")
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService uds,
                                                     PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }
}
