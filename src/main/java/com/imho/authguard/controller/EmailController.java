package com.imho.authguard.controller;

import com.imho.authguard.dto.response.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/email")
@RequiredArgsConstructor
public class EmailController {

    // Create verification request (send code to email)
    @PostMapping("/verification")
    public ResponseEntity<JsonResponse> requestVerification() {
        throw new NotImplementedException("Not implemented");
    }

    // Confirm verification with token
    @PutMapping("/verification/{token}")
    public ResponseEntity<JsonResponse> confirmVerification(@PathVariable String token) {
        throw new NotImplementedException("Not implemented");
    }

}
