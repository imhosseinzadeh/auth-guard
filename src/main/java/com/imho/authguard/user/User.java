package com.imho.authguard.user;

import com.imho.authguard.useraccess.Role;
import com.imho.authguard.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a user.
 */
@Entity
@Table(schema = "authentication", name = "users")
@Getter
@Setter
public class User extends AbstractEntity<UUID> implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToMany
    @JoinTable(
            schema = "authentication",
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    /**
     * Retrieves the authorities granted to the user.
     * Authorities include both permissions associated with roles and the roles themselves.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add permissions
        roles.forEach(
                role -> role.getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .forEach(authorities::add));

        // Add role authority
        roles.forEach(
                role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        return authorities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}
