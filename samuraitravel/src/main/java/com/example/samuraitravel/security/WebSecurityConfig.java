package com.example.samuraitravel.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // パスワードは BCrypt で照合（DB も BCrypt ハッシュで保存されていること）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security 6 推奨の MVC マッチャ
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 静的ファイルは常に許可
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(
                    mvc.pattern("/css/**"),
                    mvc.pattern("/js/**"),
                    mvc.pattern("/images/**"),
                    mvc.pattern("/storage/**")
                ).permitAll()

                // 誰でも見られるページ
                .requestMatchers(
                    mvc.pattern("/"),
                    mvc.pattern("/houses"),
                    mvc.pattern("/houses/**"),
                    mvc.pattern("/auth/**"),
                    mvc.pattern("/login"),
                    mvc.pattern("/signup"),
                    mvc.pattern("/verify"),
                    mvc.pattern("/reviews/house/**")
                ).permitAll()

                // 管理画面
                .requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")

                // それ以外はログイン必須
                .anyRequest().authenticated()
            )

            // ログイン設定（フォームの input 名と合わせる）
            .formLogin(login -> login
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")          // POST 先
                .usernameParameter("email")            // ← フォームの name と一致
                .passwordParameter("password")         // ← 同上
                .defaultSuccessUrl("/", true)
            )

            // ログアウト
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )

            // 必要に応じて CSRF 除外
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(mvc.pattern("/webhook/stripe"))
            );

        return http.build();
    }
}
