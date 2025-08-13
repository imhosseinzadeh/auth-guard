package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.AuthCode;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodeRepository extends BaseRepository<AuthCode, Long> {
}
