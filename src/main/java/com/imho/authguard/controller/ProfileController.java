package com.imho.authguard.controller;

import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.domain.service.AuthService;
import com.imho.authguard.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class ProfileController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        User authenticatedUser = authService.getAuthenticatedUser();

        UserProfileResponse response = new UserProfileResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFirstname(),
                authenticatedUser.getLastname(),
                authenticatedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<String> updateProfile() {
        throw new NotImplementedException("Not Implemented");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProfile() {
        throw new NotImplementedException("Not Implemented");
    }

}