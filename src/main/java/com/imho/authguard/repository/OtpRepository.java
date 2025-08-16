package com.imho.authguard.repository;

import com.imho.authguard.domain.entity.user.Otp;
import com.imho.authguard.domain.entity.user.OtpType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link Otp} entities.
 * Provides methods for querying active OTPs based on user and type.
 */
@Repository
public interface OtpRepository extends BaseRepository<Otp, Long> {

    /**
     * Retrieves the most recent active OTP for a given user and type.
     * An active OTP is one that:
     * <ul>
     *   <li>Has not been used (i.e., {@code usedAt} is {@code null})</li>
     *   <li>Has not expired (i.e., {@code expiresAt} is after the current timestamp)</li>
     * </ul>
     * The result is ordered by {@code issuedAt} in descending order and limited to one.
     *
     * @param userId the ID of the user associated with the OTP
     * @param type the type of OTP (e.g., LOGIN, REGISTRATION, PASSWORD_RESET)
     * @return an {@link Optional} containing the most recent active OTP, if any
     */
    default Optional<Otp> findActiveOtp(UUID userId, OtpType type) {
        return findActiveOtpsByUserIdAndType(userId, type, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    /**
     * Retrieves a list of active OTPs for a given user and type.
     * Active OTPs are defined as:
     * <ul>
     *   <li>Unused ({@code usedAt} is {@code null})</li>
     *   <li>Unexpired ({@code expiresAt} is after the current timestamp)</li>
     * </ul>
     * Results are ordered by {@code issuedAt} in descending order.
     *
     * @param userId the ID of the user associated with the OTPs
     * @param type the type of OTP to filter by
     * @param pageable pagination information to limit and sort results
     * @return a list of matching active {@link Otp} entities
     */
    @Query("""
            SELECT otp
            FROM Otp otp
            WHERE otp.user.id = :userId
              AND otp.type = :type
              AND otp.usedAt IS NULL
              AND otp.expiresAt > CURRENT_TIMESTAMP
            ORDER BY otp.issuedAt DESC
            """)
    List<Otp> findActiveOtpsByUserIdAndType(UUID userId, OtpType type, Pageable pageable);
}
