package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpAndPurposeAndIsUsedFalseAndExpiresAtGreaterThan(
            String email, String otp, String purpose, LocalDateTime now);
    
    Optional<OtpVerification> findTopByEmailAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(
            String email, String purpose);
}
