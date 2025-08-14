package com.imho.authguard.controller;

import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.domain.service.AuthService;
import com.imho.authguard.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
