package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.Permission;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Short> {
}
