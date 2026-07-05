package com.folioframe.global.auth;

import java.security.Principal;

// STOMP 세션에 인증된 회원을 실어 나르기 위한 최소 Principal 구현체(HandlerMethodArgumentResolver는 WS 메시지 매핑에 못 쓰므로 별도로 둠)
public record StompPrincipal(Long memberId) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
