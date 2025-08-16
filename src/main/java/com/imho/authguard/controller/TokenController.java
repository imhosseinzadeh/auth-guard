package com.imho.authguard.controller;

import com.imho.authguard.domain.service.AuthService;
import com.imho.authguard.dto.request.TokenRefreshRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> tokenRefresh(@Validated @RequestBody TokenRefreshRequest request) {
        Map<String, String> tokens = authService.generateFullToken(request.refreshToken());

        return ResponseEntity.ok(tokens);
    }

}
