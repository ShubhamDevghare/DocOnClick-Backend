package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsVerificationDTO {
    private Long documentsVerificationId;
    private Long doctorId;
    private String doctorName;
    private String governmentIdProof;
    private String medicalRegistrationCertificate;
    private String educationalCertificate;
    private String experienceCertificate;
    private String specializationCertificate;
    private VerificationStatus verificationStatus;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    
    // Fields for file uploads (not stored in database)
    private MultipartFile governmentIdProofFile;
    private MultipartFile medicalRegistrationCertificateFile;
    private MultipartFile educationalCertificateFile;
    private MultipartFile experienceCertificateFile;
    private MultipartFile specializationCertificateFile;
}
