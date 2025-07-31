package com.lakshit.doconclick.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_appointment_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAppointmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl; // Cloudinary URL

    @Column(nullable = false)
    private String fileType; // pdf, jpg, png, etc.

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Column
    private String description;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    // Helper method to get patient from appointment
    public Patient getPatient() {
        return appointment != null ? appointment.getPatient() : null;
    }

    // Helper method to get doctor from appointment
    public Doctor getDoctor() {
        return appointment != null ? appointment.getDoctor() : null;
    }
}
