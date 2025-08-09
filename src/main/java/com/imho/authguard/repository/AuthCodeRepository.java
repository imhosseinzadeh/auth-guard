package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.VerificationCode;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends BaseRepository<VerificationCode, Long> {
}
