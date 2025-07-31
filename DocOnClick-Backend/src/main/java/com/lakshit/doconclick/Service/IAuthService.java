package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PasswordResetConfirmDTO;
import com.lakshit.doconclick.DTO.PasswordResetRequestDTO;

public interface IAuthService {
    void requestPasswordReset(PasswordResetRequestDTO requestDTO);
    
    boolean confirmPasswordReset(PasswordResetConfirmDTO confirmDTO);
    String generateOtp();
    boolean verifyOtp(String email, String otp, String purpose);
}
