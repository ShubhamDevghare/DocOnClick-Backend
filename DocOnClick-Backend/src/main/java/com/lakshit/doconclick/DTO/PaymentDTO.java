package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long appointmentId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String receiptNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for enhanced display
    private String patientName;
    private String doctorName;
    private String patientPhone;
    private String doctorSpecialization;
    private String appointmentDate;
    private String appointmentTime;
}
