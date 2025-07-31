package com.lakshit.doconclick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileType; // LAB_REPORT, PRESCRIPTION, X_RAY, etc.

    @Column
    private String description;

    @Column(nullable = false)
    private Long fileSize;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String uploadedBy; // DOCTOR, PATIENT
}
