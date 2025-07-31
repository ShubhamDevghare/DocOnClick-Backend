package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.DoctorReviewDTO;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorReview;
import com.lakshit.doconclick.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class DoctorReviewMapper {

    public DoctorReview toEntity(DoctorReviewDTO dto, Doctor doctor, Patient patient, Appointment appointment) {
        if (dto == null) {
            return null;
        }

        return DoctorReview.builder()
                .id(dto.getId())
                .doctor(doctor)
                .patient(patient)
                .appointment(appointment)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }

    public DoctorReviewDTO toDTO(DoctorReview entity) {
        if (entity == null) {
            return null;
        }

        return DoctorReviewDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .patientId(entity.getPatient().getPatientId())
                .appointmentId(entity.getAppointment().getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .patientName(entity.getPatient().getFullName())
                .build();
    }
}
