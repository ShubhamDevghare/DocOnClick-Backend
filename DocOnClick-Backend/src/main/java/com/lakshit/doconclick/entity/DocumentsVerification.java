package com.lakshit.doconclick.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.lakshit.doconclick.enums.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentsVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documents_verification_id")
    private Long documentsVerificationId;

    @OneToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "government_id_proof")
    private String governmentIdProof;

    @Column(name = "medical_registration_certificate")
    private String medicalRegistrationCertificate;

    @Column(name = "educational_certificate")
    private String educationalCertificate;

    @Column(name = "experience_certificate")
    private String experienceCertificate;

    @Column(name = "specialization_certificate")
    private String specializationCertificate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
