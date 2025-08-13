package com.imho.authguard.domain.entity.user;

import com.imho.authguard.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(schema = "authentication", name = "verification_codes")
@Getter
@Setter
@NoArgsConstructor
public class AuthCode extends BaseEntity<Long> {

    private static final int EXPIRES_IN_MINUTE = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_code_id")
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private ZonedDateTime issuedAt;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    private ZonedDateTime verifiedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private AuthCode(String code, ZonedDateTime issuedAt, ZonedDateTime expiresAt, User user) {
        this.code = code;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public boolean isExpired() {
        return ZonedDateTime.now().isAfter(expiresAt);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public void verify() {
        if (isExpired()) {
            throw new IllegalStateException("Cannot confirm an expired token");
        }
        this.verifiedAt = ZonedDateTime.now();
    }

    public static AuthCode generateVerificationToken(User user) {
        ZonedDateTime now = ZonedDateTime.now();

        return new AuthCode(
                UUID.randomUUID().toString(),
                now,
                now.plusMinutes(EXPIRES_IN_MINUTE),
                user
        );
    }
}
