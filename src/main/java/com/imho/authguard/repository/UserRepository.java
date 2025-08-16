package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.authorities"})
    Optional<User> findWithRolesAndAuthoritiesByEmail(String email);

    boolean existsByEmail(String email);
}
