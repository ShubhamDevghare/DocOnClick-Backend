// Enhanced AppointmentResponseDTO with missing fields
package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.AppointmentStatus;
import com.lakshit.doconclick.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    
    private Long appointmentId;
    private Long doctorId; // Added for rating functionality
    private Long patientId; // Added for patient operations
    private String doctorName;
    private String doctorSpecialization;
    private String doctorProfileImage;
    private String patientName; // Added for display in appointment header
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus appointmentStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal consultationFee; // Added to fix fee display
    private BigDecimal fees; // Alternative field name
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
