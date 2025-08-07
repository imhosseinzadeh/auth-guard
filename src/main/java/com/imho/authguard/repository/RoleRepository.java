package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Short> {
    Optional<Role> findByName(String name);
}
