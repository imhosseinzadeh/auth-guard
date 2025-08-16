package com.imho.authguard.domain.entity.user;

import com.imho.authguard.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(
        schema = "authentication",
        name = "otp",
        indexes = {
                @Index(name = "idx_otp_code", columnList = "code"),
                @Index(name = "idx_otp_user", columnList = "user_id"),
                @Index(name = "idx_otp_user_code", columnList = "user_id, code")
        })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Otp extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;

    @Column(name = "otp_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private ZonedDateTime issuedAt;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    private ZonedDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Otp(OtpType type, String code, ZonedDateTime issuedAt, ZonedDateTime expiresAt, User user) {
        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("Expiration time must be after issuance time");
        }
        this.type = type;
        this.code = code;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public boolean isExpired() {
        return ZonedDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markAsUsed() {
        if (isExpired()) {
            throw new IllegalStateException("Cannot use an expired OTP");
        }

        if (isUsed()) {
            throw new IllegalStateException("Cannot use an already used OTP");
        }

        this.usedAt = ZonedDateTime.now();
    }

}
