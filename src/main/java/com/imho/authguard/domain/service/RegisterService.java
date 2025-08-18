package com.imho.authguard.domain.service;

import com.imho.authguard.domain.entity.user.Otp;
import com.imho.authguard.domain.entity.user.OtpType;
import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.dto.request.UserRegisterRequest;
import com.imho.authguard.dto.response.RegistrationStatus;
import com.imho.authguard.dto.response.UserRegisterResponse;
import com.imho.authguard.exception.domain.DomainException;
import com.imho.authguard.infra.notification.EmailService;
import com.imho.authguard.infra.notification.MessageType;
import com.imho.authguard.repository.OtpRepository;
import com.imho.authguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterService {

    private static final long VERIFICATION_CODE_TTL_SECONDS = 120L;

    private final EmailService emailService;

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest registerRequest) {
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.email());

        if (existingUser.isPresent()) {
            return handleExistingUser(existingUser.get());
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User newUser = User.builder()
                .email(registerRequest.email())
                .password(encodedPassword)
                .firstname(registerRequest.firstname())
                .lastname(registerRequest.lastname())
                .emailVerified(false)
                .enabled(false)
                .build();
        userRepository.save(newUser);

        Otp otp = generateAndSendOTP(newUser);

        long expiresInSeconds = Duration.between(ZonedDateTime.now(), otp.getExpiresAt()).toSeconds();
        return new UserRegisterResponse(RegistrationStatus.NEW_USER_CREATED, expiresInSeconds);
    }

    private UserRegisterResponse handleExistingUser(User user) {
        if (user.isEmailVerified()) {
            throw new DomainException("Duplicate email", "An account with this email already exists.");
        }

        Optional<Otp> activeOtp = otpRepository.findActiveOtp(user.getId(), OtpType.VERIFY_EMAIL);

        ZonedDateTime expiresAt;
        if (activeOtp.isPresent()) {
            expiresAt = activeOtp.get().getExpiresAt();
        } else {
            Otp otp = generateAndSendOTP(user);
            expiresAt = otp.getExpiresAt();
        }

        long expiresInSeconds = Duration.between(ZonedDateTime.now(), expiresAt).toSeconds();
        return new UserRegisterResponse(RegistrationStatus.EXISTING_USER_UNVERIFIED, expiresInSeconds);

    }

    private Otp generateAndSendOTP(User user) {
        final String rawCode = RandomStringUtils.secure().nextNumeric(6);
        final String encodedCode = passwordEncoder.encode(rawCode);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(VERIFICATION_CODE_TTL_SECONDS);
        Otp otp = new Otp(OtpType.VERIFY_EMAIL, encodedCode, now, expiresAt, user);
        otpRepository.save(otp);

        // Send OTP with email
        Map<String, Object> variables = Map.of(
                "code", rawCode,
                "expiryMinute", Duration.ofMinutes(2)
        );
        emailService.sendEmail(user.getEmail(), MessageType.EMAIL_VERIFICATION_REQUEST, variables);

        return otp;
    }

}
