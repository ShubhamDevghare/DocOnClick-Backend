package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.PasswordResetConfirmDTO;
import com.lakshit.doconclick.DTO.PasswordResetRequestDTO;
import com.lakshit.doconclick.Service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/reset-password/request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        authService.requestPasswordReset(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Boolean> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmDTO confirmDTO) {
        boolean success = authService.confirmPasswordReset(confirmDTO);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
