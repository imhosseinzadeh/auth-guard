package com.imho.authguard.useraccess;

import com.imho.authguard.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a permission in the authorization system.
 */
@Entity
@Table(schema = "authentication", name = "permissions")
@Getter
@Setter
public class Permission extends AbstractEntity<Short> {

    @Id
    @Column(name = "permission_id")
    private Short id;

    private String name;

    private String description;
}
