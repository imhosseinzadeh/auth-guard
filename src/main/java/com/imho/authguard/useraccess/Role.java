package com.imho.authguard.useraccess;

import com.imho.authguard.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a role in the authorization system.
 */
@Entity
@Table(schema = "authentication", name = "roles")
@Getter
@Setter
public class Role extends AbstractEntity<Short> {

    @Id
    @Column(name = "role_id")
    private Short id;

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
