package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.VerificationToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends BaseRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
