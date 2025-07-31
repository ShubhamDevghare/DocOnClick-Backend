// Fix for Issue 6: Enhanced AppointmentMapper to include patient name
package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.AppointmentRequestDTO;
import com.lakshit.doconclick.DTO.AppointmentResponseDTO;
import com.lakshit.doconclick.entity.*;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public static Appointment toEntity(AppointmentRequestDTO request, User user, Doctor doctor, Patient patient, AppointmentSlot appointmentSlot) {
        return Appointment.builder()
                .user(user)
                .doctor(doctor)
                .patient(patient)
                .appointmentSlot(appointmentSlot)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .paymentStatus(request.getPaymentStatus())
                .appointmentStatus(com.lakshit.doconclick.enums.AppointmentStatus.PENDING)
                .build();
    }
    
    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctor().getDoctorId()) // Fix: Include doctorId
                .patientId(appointment.getPatient().getPatientId()) // Fix: Include patientId
                .patientName(appointment.getPatient().getFullName()) // Fix: Include patient name
                .doctorName(appointment.getDoctor().getFullName())
                .doctorProfileImage(appointment.getDoctor().getProfileImage())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .paymentStatus(appointment.getPaymentStatus())
                .appointmentStatus(appointment.getAppointmentStatus())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .consultationFee(appointment.getDoctor().getFees()) // Fix: Include consultation fee
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
