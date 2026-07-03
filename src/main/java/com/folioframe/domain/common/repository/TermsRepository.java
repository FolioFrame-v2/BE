package com.folioframe.domain.common.repository;

import com.folioframe.domain.common.entity.Terms;
import com.folioframe.domain.common.enums.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    Optional<Terms> findByType(TermsType type);
}
