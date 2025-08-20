package com.imho.authguard.domain.service;

import com.imho.authguard.domain.entity.user.OtpType;
import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.dto.request.EmailVerificationRequest;
import com.imho.authguard.exception.domain.DomainException;
import com.imho.authguard.infra.notification.EmailService;
import com.imho.authguard.infra.notification.MessageType;
import com.imho.authguard.repository.UserRepository;
import com.imho.authguard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    private final EmailService emailService;
    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    /**
     * Returns the currently authenticated user.
     *
     * @return the authenticated user
     * @throws AuthenticationCredentialsNotFoundException if authentication credentials are not available or invalid
     * @throws UsernameNotFoundException                  if no user is found for the current principal
     */
    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials not found");
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Map<String, String> generateFullToken(String refreshToken) {
        final String email = jwtUtil.extractUsername(refreshToken);
        final User user = (User) userDetailsService.loadUserByUsername(email);

        return jwtUtil.generateTokens(user);
    }

    @Transactional
    public void verifyEmail(EmailVerificationRequest emailVerificationRequest) {
        final String email = emailVerificationRequest.email();
        final String code = emailVerificationRequest.code();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("No account associated with the provided email address."));

        if (user.isEmailVerified()) {
            throw new DomainException("This email address has already been verified. Please log in.");
        }

        // Check if otp is valid
        otpService.validateOtp(user, OtpType.VERIFY_EMAIL, code);

        // Set as verified
        user.setEnabled(true);
        user.setEmailVerified(true);

        Map<String, Object> variables = Map.of("email", user.getEmail());
        emailService.sendEmail(user.getEmail(), MessageType.EMAIL_VERIFICATION_APPROVED, variables);
    }

}
