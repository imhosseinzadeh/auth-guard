package com.imho.authguard.controller;

import com.imho.authguard.dto.response.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PasswordController {

    // Change password for logged-in user
    @PutMapping("/users/me/password")
    public ResponseEntity<JsonResponse> postString() {
        throw new NotImplementedException("Not implemented");
    }

    // Request password reset (unauthenticated)
    @PostMapping("/password-reset-tokens")
    public ResponseEntity<JsonResponse> requestReset() {
        throw new NotImplementedException("Not implemented");
    }

    // Confirm password reset with token
    @PutMapping("/password-reset-tokens/{token}")
    public ResponseEntity<JsonResponse> resetPassword(@PathVariable String token) {
        throw new NotImplementedException("Not implemented");
    }

}