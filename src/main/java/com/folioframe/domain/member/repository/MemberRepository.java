package com.folioframe.domain.member.repository;

import com.folioframe.domain.member.entity.Member;
import com.folioframe.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByPhone(String phone);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
