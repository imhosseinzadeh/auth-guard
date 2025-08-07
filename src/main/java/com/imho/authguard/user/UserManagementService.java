package com.imho.authguard.user;

import com.imho.authguard.user.registeration.token.VerificationToken;
import com.imho.authguard.user.registeration.token.VerificationTokenService;
import com.imho.authguard.useraccess.Role;
import com.imho.authguard.useraccess.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing user details by email.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementService {

    private static final String USER_NOT_FOUND_MSG = "User not found with email: %s";
    private static final String EMPTY_AUTHORITIES_MSG = "User with email %s has no authorities assigned.";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserDetailsService userDetailsService;

    private final VerificationTokenService verificationTokenService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public VerificationToken registerUser(String email, String rawPassword) {
        if (userRepository.existsByEmail(email))
            throw new IllegalStateException("Email is invalid or already taken");

        String encodedPassword = passwordEncoder.encode(rawPassword);

        Role userRole = roleRepository.findByName("user")
                .orElseThrow(() -> new IllegalStateException("Default role 'user' not found"));

        User user = User.builder()
                .email(email)
                .hashedPassword(encodedPassword)
                .roles(Set.of(userRole))
                .enabled(false)
                .build();

        userRepository.save(user);

        VerificationToken verificationToken = VerificationToken.generateVerificationToken(user);
        verificationTokenService.save(verificationToken);

        log.info("Registered user with email: {}", email);
        return verificationToken;
    }

    @Transactional
    public void changePassword(String rawOldPassword, String rawNewPassword) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(rawOldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        String encodedNewPassword = passwordEncoder.encode(rawNewPassword);
        user.setHashedPassword(encodedNewPassword);

        userRepository.save(user);
    }

    protected User getCurrentUser() {
        String email = Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal())
                .map(Object::toString)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        return (User) userDetailsService.loadUserByUsername(email);
    }
}
