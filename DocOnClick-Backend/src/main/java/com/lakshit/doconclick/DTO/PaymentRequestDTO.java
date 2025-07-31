package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long appointmentId;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}