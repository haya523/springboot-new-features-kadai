package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    // 既存の SecurityFilterChain はあなたの設定を残してください（例）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 既存の設定を維持（例）
        .authorizeHttpRequests(auth -> auth
        		  .requestMatchers("/css/**","/js/**","/images/**","/storage/**",
        		                   "/signup","/verify","/","/houses","/houses/**").permitAll()
        		  .requestMatchers("/favorites/**").authenticated()
        		  .anyRequest().authenticated()
        		)

            .formLogin(login -> login
            	    .loginPage("/login")
            	    .loginProcessingUrl("/login")
            	    .usernameParameter("email")     // ★ input name="email" と一致させる
            	    .passwordParameter("password")  // ★ input name="password"
            	    .defaultSuccessUrl("/", true)
            	    .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    // パスワードエンコーダ（既存と同じ。これだけでOK）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ★ DaoAuthenticationProvider を自前で new しない
    //   フレームワークの組み立てを使う。
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
