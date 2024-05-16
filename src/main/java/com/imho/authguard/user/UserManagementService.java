package com.imho.authguard.user;

import com.imho.authguard.user.registeration.token.VerificationToken;
import com.imho.authguard.user.registeration.token.VerificationTokenService;
import com.imho.authguard.useraccess.Role;
import com.imho.authguard.useraccess.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Set;

/**
 * Service class for managing user details by email.
 */
@Service
@RequiredArgsConstructor
public class UserManagementService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "User not found with email: %s";
    private static final String EMPTY_AUTHORITIES_MSG = "User with email %s has no authorities assigned.";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final VerificationTokenService verificationTokenService;

    private final PasswordEncoder passwordEncoder;

    /**
     * Locates the user based on the given email.
     *
     * @param email the email identifying the user whose data is required.
     * @return a fully populated UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user could not be found or has no authorities.
     * @throws IllegalArgumentException  if the provided email is null or empty.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));

        if (user.getAuthorities().isEmpty()) {
            throw new UsernameNotFoundException(String.format(EMPTY_AUTHORITIES_MSG, email));
        }

        return user;
    }

    @Transactional
    public VerificationToken registerUser(String email, String rawPassword) {
        if (userRepository.existsByEmail(email))
            throw new IllegalStateException("Email is invalid or already taken");

        String encodedPassword = passwordEncoder.encode(rawPassword);

        Role userRole = roleRepository.findByName("user").orElseThrow();

        User user = User.builder()
                .email(email)
                .hashedPassword(encodedPassword)
                .roles(Set.of(userRole))
                .enabled(false)
                .build();

        userRepository.save(user);

        VerificationToken verificationToken = VerificationToken.generateVerificationToken(user);
        verificationTokenService.save(verificationToken);

        return verificationToken;
    }

    @Transactional
    public void changePassword(Principal principal, String oldPassword, String newPassword) {
        User user = (User) this.loadUserByUsername(principal.getName());

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setHashedPassword(encodedNewPassword);

        userRepository.save(user);
    }

}
