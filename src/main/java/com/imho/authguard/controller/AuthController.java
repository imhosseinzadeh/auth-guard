package com.imho.authguard.controller;

import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.domain.service.AuthService;
import com.imho.authguard.dto.request.EmailVerificationRequest;
import com.imho.authguard.dto.response.JsonResponse;
import com.imho.authguard.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        User authenticatedUser = authService.getAuthenticatedUser();

        UserProfileResponse response = new UserProfileResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFirstname(),
                authenticatedUser.getLastname(),
                authenticatedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-verification")
    public ResponseEntity<JsonResponse> verify(@Validated @RequestBody EmailVerificationRequest verificationRequest) {
        authService.verifyEmail(verificationRequest);
        return ResponseEntity.ok(new JsonResponse("email verified successfully"));
    }

}
