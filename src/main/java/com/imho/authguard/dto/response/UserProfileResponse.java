package com.imho.authguard.dto.response;

import java.util.UUID;

public record UserProfileResponse(UUID id,
                                  String firstname,
                                  String lastname,
                                  String email,
                                  Boolean emailVerified,
                                  Boolean enabled) {
}
