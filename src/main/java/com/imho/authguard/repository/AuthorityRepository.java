package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.Authority;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends BaseRepository<Authority, Short> {
}
