package com.imho.authguard.bootstrap;

import com.imho.authguard.domain.entity.user.Authority;
import com.imho.authguard.domain.entity.user.Role;
import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.repository.AuthorityRepository;
import com.imho.authguard.repository.RoleRepository;
import com.imho.authguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class AuthenticationDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Idempotency: if users already exist, skip seeding
        if (userRepository.count() > 0) {
            return;
        }

        // 1) Authorities
        Authority read = new Authority();
        read.setId((short) 1);
        read.setName("READ_PRIVILEGES");
        read.setDescription("Can read data");

        Authority write = new Authority();
        write.setId((short) 2);
        write.setName("WRITE_PRIVILEGES");
        write.setDescription("Can write data");

        Authority delete = new Authority();
        delete.setId((short) 3);
        delete.setName("DELETE_PRIVILEGES");
        delete.setDescription("Can delete data");

        authorityRepository.saveAll(List.of(read, write, delete));

        // 2) Roles
        Role admin = new Role();
        admin.setId((short) 1);
        admin.setName("ADMIN");
        admin.setDescription("Administrator with full access");
        admin.setAuthorities(new HashSet<>(Arrays.asList(read, write, delete)));

        Role user = new Role();
        user.setId((short) 2);
        user.setName("USER");
        user.setDescription("Regular user with limited access");
        user.setAuthorities(new HashSet<>(Arrays.asList(read)));

        Role moderator = new Role();
        moderator.setId((short) 3);
        moderator.setName("MODERATOR");
        moderator.setDescription("Can manage user content");
        moderator.setAuthorities(new HashSet<>(Arrays.asList(read, write)));

        roleRepository.saveAll(List.of(admin, user, moderator));

        // 3) Users
        User alice = new User();
        alice.setEmail("alice@example.com");
        alice.setEmailVerified(true);
        alice.setFirstname("Alice");
        alice.setLastname("Johnson");
        alice.setPassword(passwordEncoder.encode("Password!1"));
        alice.setEnabled(true);

        User bob = new User();
        bob.setEmail("bob@example.com");
        bob.setEmailVerified(true);
        bob.setFirstname("Bob");
        bob.setLastname("Smith");
        bob.setPassword(passwordEncoder.encode("Password!2"));
        bob.setEnabled(true);

        User carol = new User();
        carol.setEmail("carol@example.com");
        carol.setEmailVerified(true);
        carol.setFirstname("Carol");
        carol.setLastname("White");
        carol.setPassword(passwordEncoder.encode("Password!3"));
        carol.setEnabled(true);

        userRepository.saveAll(List.of(alice, bob, carol));

        // 4) Assign roles to users (User is owning side of users_roles)
        alice.setRoles(new HashSet<>(Arrays.asList(admin)));
        bob.setRoles(new HashSet<>(Arrays.asList(user)));
        bob.setRoles(new HashSet<>(Arrays.asList(user)));
        userRepository.saveAll(List.of(alice, bob, carol));
    }
}
