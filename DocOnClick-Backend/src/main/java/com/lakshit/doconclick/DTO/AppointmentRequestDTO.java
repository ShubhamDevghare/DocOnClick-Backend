package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {
    private Long userId; // Nullable when doctor books it
    private Long doctorId;
    private Long patientId;
    private Long timeSlotId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private PaymentStatus paymentStatus;
}