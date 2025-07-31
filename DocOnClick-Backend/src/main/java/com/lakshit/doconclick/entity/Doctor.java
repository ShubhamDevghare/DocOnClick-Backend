package com.lakshit.doconclick.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    private String profileImage;

    @Column(nullable = false)
    private String medicalLicenseNumber;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private BigDecimal fees;

    // Slot duration in minutes
    @Column(nullable = false)
    private Integer slotDurationMinutes = 30;

    // New field for holiday status
    @Column(nullable = false)
    private boolean isHoliday = false;

    // Add one-to-one relationship with BankingDetails
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankingDetails bankingDetails;

    // Add one-to-one relationship with DocumentsVerification
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentsVerification documentsVerification;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DoctorWeeklySchedule> doctorWeeklySchedule = new HashSet<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OverrideTimeSlot> overrideTimeSlot = new HashSet<>();

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppointmentSlot> appointmentSlot = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
