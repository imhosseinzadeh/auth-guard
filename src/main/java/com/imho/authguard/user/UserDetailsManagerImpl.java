package com.imho.authguard.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing user details by email.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final UserRepository repository;

    /**
     * Locates the user based on the given email.
     *
     * @param email the email identifying the user whose data is required.
     * @return a fully populated UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user could not be found or has no authorities.
     * @throws IllegalArgumentException  if the provided email is null or empty.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getAuthorities().isEmpty()) {
            throw new UsernameNotFoundException("User with email '" + email + "' has no authorities assigned. Please check user role and permissions.");
        }

        return user;
    }

    @Override
    public void createUser(UserDetails user) {
        throw new NotImplementedException();
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteUser(String username) {
        throw new NotImplementedException();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new NotImplementedException();
    }

    @Override
    public boolean userExists(String username) {
        throw new NotImplementedException();
    }
}
