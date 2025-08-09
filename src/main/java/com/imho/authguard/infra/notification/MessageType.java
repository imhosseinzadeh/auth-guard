package com.imho.authguard.infra.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum MessageType {

    EMAIL_VERIFICATION_REQUEST(
            "Verify Your Email Address",
            "email-verification-request.html",
            Set.of("firstname", "code", "expiryMinute")
    ),

    EMAIL_VERIFICATION_APPROVED(
            "Your Email Approved",
            "email-verification-approved.html",
            Set.of("firstname", "lastname", "email")
    ),

    PASSWORD_RESET_REQUEST(
            "Password Reset Request",
            "password-reset-request.html",
            Set.of("resetToken", "expiryMinute", "ipAddress")
    );

    private final String subject;
    private final String filename;
    private final Set<String> variables;

    public String resolveTemplatePath() {
        return String.format("email/%s", filename);
    }

    public void validateVariables(Map<String, Object> providedVariables) {
        for (String required : variables) {
            if (!providedVariables.containsKey(required)) {
                throw new IllegalArgumentException("Missing required variable: " + required);
            }
        }
    }

}
