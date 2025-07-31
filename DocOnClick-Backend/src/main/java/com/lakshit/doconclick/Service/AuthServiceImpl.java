package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PasswordResetConfirmDTO;
import com.lakshit.doconclick.DTO.PasswordResetRequestDTO;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.OtpVerificationRepository;
import com.lakshit.doconclick.Repository.UserRepository;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.OtpVerification;
import com.lakshit.doconclick.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final OtpVerificationRepository otpRepository;
    private final IEmailService emailService;
    
    private static final String PASSWORD_RESET_PURPOSE = "PASSWORD_RESET";
    private static final int OTP_EXPIRY_MINUTES = 15;

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        
        // Check if email exists in either user or doctor repository
        boolean isUser = userRepository.findByEmail(email).isPresent();
        boolean isDoctor = doctorRepository.findByEmail(email).isPresent();
        
        if (!isUser && !isDoctor) {
            throw new RuntimeException("Email not found");
        }
        
        // Check if there's an existing OTP that's not expired
        Optional<OtpVerification> existingOtp = otpRepository.findTopByEmailAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(
                email, PASSWORD_RESET_PURPOSE);
        
        if (existingOtp.isPresent() && existingOtp.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            // Reuse existing OTP if it's not expired
//            emailService.sendPasswordResetEmail(email, existingOtp.get().getOtp());
            return;
        }
        
        // Generate new OTP
        String otp = generateOtp();
        
        // Save OTP to database
        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .purpose(PASSWORD_RESET_PURPOSE)
                .isUsed(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();
        
        otpRepository.save(otpVerification);
        
        // Send OTP via email
//        emailService.sendPasswordResetEmail(email, otp);
    }

    @Override
    @Transactional
    public boolean confirmPasswordReset(PasswordResetConfirmDTO confirmDTO) {
        String email = confirmDTO.getEmail();
        String otp = confirmDTO.getOtp();
        String newPassword = confirmDTO.getNewPassword();
        
        // Verify OTP
        if (!verifyOtp(email, otp, PASSWORD_RESET_PURPOSE)) {
            return false;
        }
        
        // Update password in user or doctor repository
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setPassword(newPassword);
            doctorRepository.save(doctor);
            return true;
        }
        
        return false;
    }

    @Override
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp, String purpose) {
        Optional<OtpVerification> otpVerificationOpt = otpRepository.findByEmailAndOtpAndPurposeAndIsUsedFalseAndExpiresAtGreaterThan(
                email, otp, purpose, LocalDateTime.now());
        
        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            otpVerification.setUsed(true);
            otpRepository.save(otpVerification);
            return true;
        }
        
        return false;
    }
}
