package com.imho.authguard.domain.entity.user;

import com.imho.authguard.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Represents a role in the authorization system.
 */
@Entity
@Table(schema = "authentication", name = "roles")
@Getter
@Setter
public class Role extends BaseEntity<Short> {

    @Id
    @Column(name = "role_id")
    private Short id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            schema = "authentication",
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}
