package com.busanit501.teamboot.config;

import com.busanit501.teamboot.security.CustomUserDetailsService;
import com.busanit501.teamboot.security.handler.Custom403Handler;
import com.busanit501.teamboot.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class CustomSecurityConfig {
    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("===========config=================");

        http
                // CSRF 비활성화 (보안상 위험할 수 있음. 필요 시 활성화 및 CSRF 토큰 추가)
                .csrf(csrf -> csrf.disable())

                // 정적 리소스 허용
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/gallery/**",
                                "/member/login", "/member/join", "/board/list",
                                "/login/oauth2/code/kakao", "https://kauth.kakao.com",
                                "https://kapi.kakao.com",
                                "/error/**" // 에러 페이지 접근 허용
                        ).permitAll()
                        .requestMatchers("/communities/**", "/matching/**").authenticated()
                        .requestMatchers("/admin/**", "/board/update").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // 폼 로그인 설정 통합
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/login")
                        .defaultSuccessUrl("/board/list", true)
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/member/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                )

                // 자동 로그인 (Remember Me) 설정
                .rememberMe(rememberMe -> rememberMe
                        .key("12345678")
                        .tokenRepository(persistentTokenRepository())
                        .userDetailsService(customUserDetailsService)
                        .tokenValiditySeconds(60 * 60 * 24 * 30) // 30일
                )

                // 예외 처리 핸들러 설정
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/member/login")
                        .successHandler(authenticationSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        // repo.setCreateTableOnStartup(true); // 처음 실행 시 테이블 자동 생성 (단, 중복 실행 시 오류 발생)
        return repo;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("시큐리티 동작 확인 ====webSecurityCustomizer======================");
        return (web) ->
                web.ignoring()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
}
