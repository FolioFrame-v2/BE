package com.folioframe.global.auth;

import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.enums.MemberType;
import com.folioframe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email;
        String name;

        // 구글 로그인 처리
        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        // DB 조회 후 없으면 가입
        Member member = memberRepository.findByLoginId(email)
                .orElseGet(() -> registerNewMember(email, name));

        return new CustomUserDetails(member, attributes);
    }

    private Member registerNewMember(String email, String name) {
        Member newMember = Member.builder()
                .loginId(email)
                .password("SOCIAL_LOGIN")
                .memberType(MemberType.TALENT)
                .build();

        return memberRepository.save(newMember);
    }
}