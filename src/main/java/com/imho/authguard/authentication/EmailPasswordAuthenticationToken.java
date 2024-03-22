package com.imho.authguard.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class EmailPasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private String password;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>EmailPasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     */
    private EmailPasswordAuthenticationToken(Object principal, String password) {
        super(null);
        this.principal = principal;
        this.password = password;
        super.setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     */
    public EmailPasswordAuthenticationToken(Object principal, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.password = password;
        super.setAuthenticated(true);
    }


    /**
     * This factory method can be safely used by any code that wishes to create an
     * unauthenticated <code>EmailPasswordAuthenticationToken</code>.
     *
     * @return EmailPasswordAuthenticationToken with false isAuthenticated() result
     */
    public static EmailPasswordAuthenticationToken unauthenticated(Object principal, String password) {
        return new EmailPasswordAuthenticationToken(principal, password);
    }

    /**
     * This factory method can be safely used by any code that wishes to create an
     * authenticated <code>EmailPasswordAuthenticationToken</code>.
     *
     * @return EmailPasswordAuthenticationToken with true isAuthenticated() result
     */
    public static EmailPasswordAuthenticationToken authenticated(Object principal, String password, Collection<? extends GrantedAuthority> authorities) {
        return new EmailPasswordAuthenticationToken(principal, password, authorities);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String getCredentials() {
        return this.password;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.password = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EmailPasswordAuthenticationToken that = (EmailPasswordAuthenticationToken) o;
        return Objects.equals(principal, that.principal) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principal, password);
    }
}
