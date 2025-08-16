package com.imho.authguard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(@Email String email,
                                  @Size(min = 8) String password,
                                  @NotBlank String firstname,
                                  @NotBlank String lastname) {
}