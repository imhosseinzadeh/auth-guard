package com.imho.authguard.domain.service;

import com.imho.authguard.domain.entity.user.Otp;
import com.imho.authguard.domain.entity.user.OtpType;
import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.exception.domain.DomainException;
import com.imho.authguard.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for validating OTPs (One-Time Passwords)
 * for various authentication flows such as verify email or reset password.
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String ERROR_MESSAGE_MISSING_OTP = "No valid OTP was found for this account. Please request a new one.";

    private static final String ERROR_MESSAGE_INVALID_OTP = "The OTP you entered is incorrect. Please check and try again.";

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Validates a one-time password (OTP) for a given user and OTP type.
     * <p>
     * The method performs the following checks:
     * <ul>
     *   <li>Ensures an active OTP exists for the user and type</li>
     *   <li>Verifies that the provided raw code matches the stored encoded OTP</li>
     *   <li>Marks the OTP as used if validation succeeds</li>
     * </ul>
     *
     * @param user    the user associated with the OTP
     * @param type    the type of OTP (e.g., VERIFY_EMAIL, RESET_PASSWORD)
     * @param rawCode the raw OTP code entered by the user
     * @throws DomainException if no valid OTP is found or the code is incorrect
     */
    public void validateOtp(User user, OtpType type, String rawCode) {
        Otp otp = otpRepository.findActiveOtp(user.getId(), type)
                .orElseThrow(() -> new DomainException(ERROR_MESSAGE_MISSING_OTP));

        if (!passwordEncoder.matches(rawCode, otp.getCode())) {
            throw new DomainException(ERROR_MESSAGE_INVALID_OTP);
        }

        otp.markAsUsed();
    }

}

