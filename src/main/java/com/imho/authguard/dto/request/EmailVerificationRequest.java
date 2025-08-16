package com.imho.authguard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EmailVerificationRequest(@Email String email, @Size(min = 6, max = 6) String code) {
}
