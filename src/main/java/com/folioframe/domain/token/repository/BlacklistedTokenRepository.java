package com.folioframe.domain.token.repository;

import com.folioframe.domain.token.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    Boolean existsByToken(String token);
}
