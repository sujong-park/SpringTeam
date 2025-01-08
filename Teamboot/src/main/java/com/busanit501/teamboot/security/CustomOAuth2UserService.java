package com.busanit501.teamboot.security;

import com.busanit501.teamboot.domain.Member;
import com.busanit501.teamboot.domain.MemberRole;
import com.busanit501.teamboot.repository.MemberRepository;
import com.busanit501.teamboot.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 멤버와 연동할 준비, 준비물 1) MemberRepository, 2) PasswordEncoder
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 소셜 로그인시, 반환 타입 : OAuth2User
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("kakao login CustomOAuth2UserService userRequest");
        log.info(userRequest);

        // userRequest: 소셜 유저의 정보 담겨 있음.
        // 이메일 추출 해보기.
        ClientRegistration clientRegistration
                = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("clientName : " + clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();
        //paramMap
        //키: properties, 값 : 객체 안에(맵구조), nickname=이상용
        //키: properties, 값 : 객체 안에(맵구조), profile_image=http://k.kakaocdn.net/dn/IbxaJ/btsJ3hcSYmJ/6zalcIKKnKf3gJNqEusBo0/img_640x640.jpg
        //키: kakao_account, 값 : 객체 안에(맵구조), email=lsy3709@kakao.com

        // 소셜 정보 출력 확인용
        paramMap.forEach((key, value) -> {
            log.info("key : " + key + " value : " + value);
        });

        String email = null;

        switch (clientName) {
            case "kakao":
                //getKakaoEmail(paramMap) , 메서드를 밑에 정의
                email = getKakaoEmail(paramMap);
                break;
        }

        // 반환 타입을 MemberSecurityDTO 타입으로 반환 하면 됨.

        //generateDTO(email, paramMap) , 메서드 정의
        return generateDTO(email, paramMap);
    }

    private String getKakaoEmail(Map<String, Object> params) {
        Object value = params.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");
        log.info("email : " + email);
        return email;
    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        Optional<Member> result = memberRepository.findByEmail(email);

        // 사용자가, 데이터베이스에 없으면, , 새로 생성.
        // 소셜 로그인해서, 처음 유저를 만들 경우, 임의로 패스워드 1111 로 설정.
        if (result.isEmpty()) {
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            // 화면에 출력 용도 사용하기 위해서, DTO로 변환
            // 로그인 할 때도 필요함.
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email, "1111", email, false,
                            true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            // 소셜 로그인 정보 담기.
            memberSecurityDTO.setProps(params);
            return memberSecurityDTO;
        } else {
            // 유저가 있는 경우,
            Member member = result.get();
            //
            MemberSecurityDTO memberSecurityDTO
                    = new MemberSecurityDTO(
                    member.getMid(),
                    member.getMpw(),
                    member.getEmail(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet().stream().map(
                            memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())
                    ).collect(Collectors.toList())
            );
            return memberSecurityDTO;
        }


    }

}
