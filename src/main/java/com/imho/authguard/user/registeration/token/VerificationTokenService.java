package com.imho.authguard.user.registeration.token;

import com.imho.authguard.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public void save(VerificationToken token) {
        verificationTokenRepository.save(token);
    }

    @Transactional
    public String verify(String tokenValue) {
        VerificationToken verificationToken = getVerificationToken(tokenValue);

        if (verificationToken.getVerifiedAt() != null) throw new IllegalStateException("Email already verified");
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalStateException("Token expired");

        verifyTokenAndUpdateUser(verificationToken);

        return verificationToken.getUser().getEmail();
    }

    private VerificationToken getVerificationToken(String tokenValue) {
        return verificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
    }

    private void verifyTokenAndUpdateUser(VerificationToken verificationToken) {
        verificationToken.setVerifiedAt(LocalDateTime.now());

        User user = verificationToken.getUser();
        user.setEnabled(true);

        verificationTokenRepository.save(verificationToken);
    }
}
