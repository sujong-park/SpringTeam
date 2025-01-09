//package com.busanit501.teamboot.config;
//
//import com.busanit501.teamboot.security.CustomUserDetailsService;
//import com.busanit501.teamboot.security.handler.Custom403Handler;
//import com.busanit501.teamboot.security.handler.CustomSocialLoginSuccessHandler;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//
//import javax.sql.DataSource;
//
//@Log4j2
//@Configuration
//@RequiredArgsConstructor
//// 시큐리티 설정 on 추가
//@EnableWebSecurity
//// 권한별 설정 추가
//// 이전 문법 ://@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableMethodSecurity()
//public class CustomSecurityConfig {
//    // 자동 로그인 순서1,
//    private final DataSource dataSource;
//    // 시큐리티에서 로그인 처리를 담당하는 도구
//    private final CustomUserDetailsService customUserDetailsService;
//    // 자동 로그인 순서1,
//
//    //순서1,
//    // 인증, 인가 관련 구체적인 설정은 여기 메서드에서 작성
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        log.info("===========config=================");
//
//        // 순서3, 로그인 방식을 폼 로그인으로 설정.
//        // 옛날 문법,
//        // http.formLogin();
//        http.formLogin(
//                formLogin ->
//                        formLogin.loginPage("/member/login")
//        );
//
//        // 순서4
//        //로그인 후, 성공시 리다이렉트 될 페이지 지정, 간단한 버전.
//        http.formLogin(formLogin ->
//                formLogin.defaultSuccessUrl("/board/list",true)
//        );
//
//        // 순서5
//        // 기본은 csrf 설정이 on, 작업시에는 끄고 작업하기.
//        // 만약, 사용한다면,
//        // 웹 화면에서 -> 서버로,  csrf 토큰 생성해서 전송.
//        // 레스트로 작업시에도 , csrf 토큰 생성해서 전송.
//        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
//
//
//        // 순서 6, 가장 중요함.
//        // 시큐리티의 전체 허용 여부 관련 목록
//        // 주의사항, 위에서 부터 차례대로 설정 적용이 됨.
//        // 첫번째 줄에 너무 큰 범위로 막는 설정을 하고, 다음 줄에서 허용을해도
//        // 허용이 안됩니다.
//        http.authorizeHttpRequests(
//                authorizeRequests -> {
//                    authorizeRequests.requestMatchers
//                            ("/css/**", "/js/**","/images/**","gallery/**",
//                                    "/member/login","/member/join", "/board/list",
//                                    "http://localhost:8080/login/oauth2/code/kakao",
//                                    "https://kauth.kakao.com",
//                                    "https://kapi.kakao.com").permitAll();
//                    authorizeRequests.requestMatchers
//                            ("/board/register").authenticated();
//                    authorizeRequests.requestMatchers
//                            ("/admin/**","/board/update").hasRole("ADMIN");
//                    //위의 3가지 조건을 제외한 나머지 모든 접근은 인증이 되어야 접근이 가능함.
//                    authorizeRequests
//                            .anyRequest().authenticated();
////                            .anyRequest().permitAll();
//                }
//
//        );
//
//        // 순서 8, 로그아웃 설정.
//        // 로그 아웃 설정.
//        // 작업 진행 순서,
//        // 웹브라우저 -> http://localhost:8080/member/logout
//        // 시큐리티가 동작을하고, 로그아웃 처리를 자동으로 하고,
//        // 로그 아웃 성공시, 성공 후 이동할 페이지로 이동 시킴.
//        // ?logout , 파라미터,
//        // /member/login?logout
//        // 멤버 컨트롤러,
//        //
//        http.logout(
//                logout -> logout.logoutUrl("/member/logout")
//                        .logoutSuccessUrl("/member/login")
//
//        );
//
//        // 자동 로그인 순서2,
//        http.rememberMe(
//                httpSecurityRememberMeConfigurer
//                -> httpSecurityRememberMeConfigurer.key("12345678")
//                        .tokenRepository(persistentTokenRepository()) // 밑에서, 토큰 설정 추가해야해서,
//                        .userDetailsService(customUserDetailsService)
//                        .tokenValiditySeconds(60*60*24*30) //30일
//        );
//
//        // 자동 로그인 순서2,
//
//        // 403 에러 페이지 연결 하기.
//        http.exceptionHandling(
//                exception -> {
//            exception.accessDeniedHandler(accessDeniedHandler());
//        });
//
//        //카카오 로그인 API 설정
//        http.oauth2Login(
//                oauthLogin -> {
//                    oauthLogin.loginPage("/member/login");
//                    // 카카오 로그인 후 , 후처리 적용하기.
//                    oauthLogin.successHandler(authenticationSuccessHandler());
//                }
//        );
//
//
//
//
//        return http.build();
//    }
//
//    // 자동 로그인 순서3,
//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        // 시큐리에서 정의 해둔 구현체
//        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
//        repo.setDataSource(dataSource);
//        return repo;
//    }
//    // 자동 로그인 순서3,
//
//
//    // 순서2,
//    // css, js, 등 정적 자원은 시큐리티 필터에서 제외하기
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        log.info("시큐리티 동작 확인 ====webSecurityCustomizer======================");
//        return (web) ->
//                web.ignoring()
//                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }
//
//    //순서7, 패스워드 암호화를 해주는 도구, 스프링 설정.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 403 핸들러 추가.
//    // 설정 클래스에 추가하기.
//    // 레스트용, Content-Type, application/json 형태 일 때만 동작을하고,
//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return new Custom403Handler();
//    }
//
//    // 소셜 로그인 후, 후처리 추가, 설정,
//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return new CustomSocialLoginSuccessHandler(passwordEncoder());
//    }
//
//
//
//}

//테스트용
package com.busanit501.teamboot.config;

import com.busanit501.teamboot.security.CustomUserDetailsService;
import com.busanit501.teamboot.security.handler.Custom403Handler;
import com.busanit501.teamboot.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Log4j2 // 로그 출력을 위한 어노테이션
@Configuration // Spring Configuration 클래스임을 명시
@RequiredArgsConstructor // 생성자를 통한 의존성 주입
@EnableWebSecurity // Spring Security를 활성화
@EnableMethodSecurity // 메서드 수준에서의 보안 설정 활성화
public class CustomSecurityConfig {

    // 데이터베이스 연결 객체(DataSource) - 자동 로그인에 사용
    private final DataSource dataSource;

    // 사용자 인증 정보를 처리하는 커스텀 서비스
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Spring Security의 핵심 설정을 담당하는 메서드
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 예외 처리
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("===========config=================");

        // 로그인 페이지 설정
        http.formLogin(formLogin -> formLogin
                .loginPage("/member/login") // 커스텀 로그인 페이지 URL
                .defaultSuccessUrl("/board/list", true) // 로그인 성공 시 리다이렉트될 URL
        );

        // CSRF 보호 비활성화 (개발 및 테스트 용도에서만 사용)
        http.csrf(csrf -> csrf.disable());

        // URL별 접근 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**", "/gallery/**",
                        "/member/login", "/member/join", "/board/list").permitAll() // 비인증 사용자도 접근 가능
                .requestMatchers("/board/register").authenticated() // 인증 사용자만 접근 가능
                .requestMatchers("/admin/**", "/board/update").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/member/logout") // 로그아웃 요청 URL
                .logoutSuccessUrl("/member/login") // 로그아웃 성공 후 리다이렉트될 URL
        );

        // 자동 로그인(Remember Me) 설정
        http.rememberMe(rememberMe -> rememberMe
                .key("12345678") // Remember Me를 위한 키
                .tokenRepository(persistentTokenRepository()) // 토큰 저장소 설정
                .userDetailsService(customUserDetailsService) // 사용자 인증 서비스 설정
                .tokenValiditySeconds(60 * 60 * 24 * 30) // 토큰 유효기간 (30일)
        );

        // 403(권한 없음) 에러 처리 핸들러 설정
        http.exceptionHandling(exception -> exception
                .accessDeniedHandler(accessDeniedHandler()) // 커스텀 403 에러 핸들러 등록
        );

        // OAuth2 로그인 설정 (예: 카카오 로그인 등)
        http.oauth2Login(oauthLogin -> oauthLogin
                .loginPage("/member/login") // 소셜 로그인 요청 URL
                .successHandler(authenticationSuccessHandler()) // 로그인 성공 후처리 핸들러
        );

        return http.build(); // 설정 완료 후 필터 체인 반환
    }

    /**
     * Remember Me 기능을 위한 PersistentTokenRepository Bean 생성
     * @return PersistentTokenRepository
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource); // 데이터베이스와 연결
        return repo;
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder Bean 생성
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 방식의 비밀번호 암호화 사용
    }

    /**
     * 403(권한 없음) 에러 처리 핸들러 Bean 생성
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler(); // 커스텀 403 에러 핸들러 반환
    }

    /**
     * 소셜 로그인 성공 후처리 핸들러 Bean 생성
     * @return AuthenticationSuccessHandler
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder()); // 성공 핸들러에 PasswordEncoder 주입
    }

    /**
     * AuthenticationManager를 Bean으로 등록
     * @param authConfig AuthenticationConfiguration 객체
     * @return AuthenticationManager
     * @throws Exception 예외 처리
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // AuthenticationManager 반환
    }
}
