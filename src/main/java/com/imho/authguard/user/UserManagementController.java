package com.imho.authguard.user;

import com.imho.authguard.user.registeration.token.VerificationToken;
import com.imho.authguard.user.registeration.token.VerificationTokenService;
import com.imho.authguard.user.request.ChangePasswordRequest;
import com.imho.authguard.user.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignupRequest signupRequest) {
        VerificationToken verificationToken = this.userManagementService.registerUser(signupRequest.email(), signupRequest.password());
        return ResponseEntity.ok(verificationToken.getToken());
    }

    @PutMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam("verificationToken") String verificationToken) {
        String verifiedUserEmail = verificationTokenService.verify(verificationToken);
        return ResponseEntity.ok().body(verifiedUserEmail);
    }

    @PutMapping("/change-password")
    public void changePassword(Principal principal, @RequestBody ChangePasswordRequest changePasswordRequest) {
        String oldPassword = changePasswordRequest.oldPassword();
        String newPassword = changePasswordRequest.newPassword();

        userManagementService.changePassword(principal, oldPassword, newPassword);
    }

}
