package com.imho.authguard.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * Represents a base class for entities in the application.
 * <p>
 * Entities should extend this class and provide concrete implementations
 * for the {@code getId()} and {@code setId(I id)} methods.
 * <p>
 * The generic type {@code <I>} represents the type of the entity's ID,
 * which must be serializable.
 *
 * @param <I> The type of the entity's ID, which must extend {@link Serializable}.
 */
@Getter
public abstract class AbstractEntity<I extends Serializable> implements Serializable {

    public abstract I getId();

    public abstract void setId(I id);
}
