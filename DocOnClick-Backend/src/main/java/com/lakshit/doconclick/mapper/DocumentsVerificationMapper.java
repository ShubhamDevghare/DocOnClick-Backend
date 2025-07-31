package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.DocumentsVerificationDTO;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DocumentsVerification;
import org.springframework.stereotype.Component;

@Component
public class DocumentsVerificationMapper {

    public DocumentsVerification toEntity(DocumentsVerificationDTO dto, Doctor doctor) {
        if (dto == null) {
            return null;
        }

        return DocumentsVerification.builder()
                .documentsVerificationId(dto.getDocumentsVerificationId())
                .doctor(doctor)
                .governmentIdProof(dto.getGovernmentIdProof())
                .medicalRegistrationCertificate(dto.getMedicalRegistrationCertificate())
                .educationalCertificate(dto.getEducationalCertificate())
                .experienceCertificate(dto.getExperienceCertificate())
                .specializationCertificate(dto.getSpecializationCertificate())
                .verificationStatus(dto.getVerificationStatus())
                .build();
    }

    public DocumentsVerificationDTO toDTO(DocumentsVerification entity) {
        if (entity == null) {
            return null;
        }

        return DocumentsVerificationDTO.builder()
                .documentsVerificationId(entity.getDocumentsVerificationId())
                .doctorId(entity.getDoctor().getDoctorId())
                .doctorName(entity.getDoctor().getFullName())
                .governmentIdProof(entity.getGovernmentIdProof())
                .medicalRegistrationCertificate(entity.getMedicalRegistrationCertificate())
                .educationalCertificate(entity.getEducationalCertificate())
                .experienceCertificate(entity.getExperienceCertificate())
                .specializationCertificate(entity.getSpecializationCertificate())
                .verificationStatus(entity.getVerificationStatus())
                .uploadedAt(entity.getUploadedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
