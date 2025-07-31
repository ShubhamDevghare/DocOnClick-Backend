package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.PaymentDTO;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentDTO dto, Appointment appointment) {
        if (dto == null) {
            return null;
        }

        return Payment.builder()
                .id(dto.getId())
                .appointment(appointment)
                .razorpayOrderId(dto.getRazorpayOrderId())
                .razorpayPaymentId(dto.getRazorpayPaymentId())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .receiptNumber(dto.getReceiptNumber())
                .build();
    }

    public PaymentDTO toDTO(Payment entity) {
        if (entity == null) {
            return null;
        }

        PaymentDTO.PaymentDTOBuilder builder = PaymentDTO.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment() != null ? entity.getAppointment().getId() : null)
                .razorpayOrderId(entity.getRazorpayOrderId())
                .razorpayPaymentId(entity.getRazorpayPaymentId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .receiptNumber(entity.getReceiptNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Add appointment details if available
        if (entity.getAppointment() != null) {
            Appointment appointment = entity.getAppointment();
            
            if (appointment.getPatient() != null) {
                builder.patientName(appointment.getPatient().getFullName())
                       .patientPhone(appointment.getPatient().getPhone());
            }
            
            if (appointment.getDoctor() != null) {
                builder.doctorName(appointment.getDoctor().getFullName())
                       .doctorSpecialization(appointment.getDoctor().getSpecialization());
            }
            
            if (appointment.getAppointmentDate() != null) {
                builder.appointmentDate(appointment.getAppointmentDate().toString());
            }
            
            if (appointment.getAppointmentTime() != null) {
                builder.appointmentTime(appointment.getAppointmentTime().toString());
            }
        }

        return builder.build();
    }
}
