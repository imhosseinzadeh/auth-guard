package com.imho.authguard.dto.response;

public record UserRegisterResponse(RegistrationStatus registrationStatus, Long otpExpiresInSeconds) {
}

