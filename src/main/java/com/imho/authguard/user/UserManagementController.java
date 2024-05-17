package com.imho.authguard.user;

import com.imho.authguard.user.registeration.token.VerificationToken;
import com.imho.authguard.user.registeration.token.VerificationTokenService;
import com.imho.authguard.user.request.ChangePasswordRequest;
import com.imho.authguard.user.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignupRequest signupRequest) {
        VerificationToken verificationToken = this.userManagementService.registerUser(signupRequest.email(), signupRequest.password());
        return new ResponseEntity<>(verificationToken.getToken(), HttpStatus.CREATED);
    }

    @PutMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam("verificationToken") String verificationToken) {
        try {
            String verifiedUserEmail = verificationTokenService.verify(verificationToken);
            return ResponseEntity.ok().body(verifiedUserEmail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        try {
            String rawOldPassword = changePasswordRequest.oldPassword();
            String rawNewPassword = changePasswordRequest.newPassword();

            userManagementService.changePassword(rawOldPassword, rawNewPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to change password: " + e.getMessage());
        }
    }

}
