package com.folioframe.domain.token.repository;

import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String refreshToken);

    //기존 토큰 존재 여부 확인
    Optional<RefreshToken> findByMember(Member member);

    //로그인시 기존 토큰 삭제용
    void deleteByMember(Member member);

    @Transactional
    @Modifying
    void deleteByToken(String refreshToken);
}
