package com.imho.authguard.controller;

import com.imho.authguard.domain.service.RegisterService;
import com.imho.authguard.dto.request.UserRegisterRequest;
import com.imho.authguard.dto.response.RegistrationStatus;
import com.imho.authguard.dto.response.UserRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        final UserRegisterResponse response = registerService.register(userRegisterRequest);

        HttpStatus status = response.registrationStatus() == RegistrationStatus.NEW_USER_CREATED
                ? HttpStatus.CREATED
                : HttpStatus.OK;

        return ResponseEntity
                .status(status)
                .body(response);
    }

}
