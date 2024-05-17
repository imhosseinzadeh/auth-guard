package com.imho.authguard.authentication;

import com.imho.authguard.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        User user = (User) userDetailsService.loadUserByUsername(authentication.getName());

        String requestPassword = authentication.getCredentials().toString();
        String storedPassword = user.getPassword();

        if (!passwordEncoder.matches(requestPassword, storedPassword))
            throw new BadCredentialsException("Password doesn't match");

        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        user.eraseCredentials();

        return EmailPasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

