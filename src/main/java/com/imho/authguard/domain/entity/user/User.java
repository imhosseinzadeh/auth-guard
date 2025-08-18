package com.imho.authguard.domain.entity.user;

import com.imho.authguard.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity<UUID> implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstname;

    private String lastname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    private boolean emailVerified;

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
     * Authorities include both authorities associated with roles and the roles themselves.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add authorities
        roles.forEach(
                role -> role.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .forEach(authorities::add));

        // Add role authority
        roles.forEach(
                role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

}
