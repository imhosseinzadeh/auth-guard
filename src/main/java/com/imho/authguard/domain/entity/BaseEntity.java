package com.imho.authguard.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Base entity class with versioning and timestamp support.
 * All entities should extend this class.
 *
 * @param <I> Entity ID type (must be Serializable)
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity<I extends Serializable> implements Serializable {

    @Version
    private Long version;

    @Column(updatable = false)
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private ZonedDateTime createdAt;

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private ZonedDateTime updatedAt;

    public abstract I getId();

    public abstract void setId(I id);

    public boolean isNew() {
        return getId() == null;
    }

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

}
