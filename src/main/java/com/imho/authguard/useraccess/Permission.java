package com.imho.authguard.useraccess;

import com.imho.authguard.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a permission in the authorization system.
 */
@Entity
@Table(schema = "authentication", name = "permissions")
@Getter
@Setter
public class Permission extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private UUID id;

    private String name;

    private String description;
}
