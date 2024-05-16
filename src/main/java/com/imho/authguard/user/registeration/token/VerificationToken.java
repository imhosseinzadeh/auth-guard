package com.imho.authguard.user.registeration.token;

import com.imho.authguard.common.AbstractEntity;
import com.imho.authguard.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "authentication", name = "verification_token")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends AbstractEntity<Long> {

    private static final int EXPIRES_IN_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime verifiedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private User user;

    private VerificationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public static VerificationToken generateVerificationToken(User user) {
        return new VerificationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(EXPIRES_IN_MINUTE),
                user
        );
    }
}
