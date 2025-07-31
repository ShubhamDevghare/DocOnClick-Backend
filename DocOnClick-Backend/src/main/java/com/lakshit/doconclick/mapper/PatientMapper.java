package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.PatientRequestDTO;
import com.lakshit.doconclick.DTO.PatientResponseDTO;
import com.lakshit.doconclick.entity.Patient;

public class PatientMapper {
    public static PatientResponseDTO toResponseDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .patientId(patient.getPatientId())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .emailAddress(patient.getEmailAddress())
                .dateOfBirth(patient.getDateOfBirth())
                .address(patient.getAddress())
                .role(patient.getRole())
                .build();
    }

    public static Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .phone(dto.getPhone())
                .emailAddress(dto.getEmailAddress())
                .address(dto.getAddress())
                .role(dto.getRole())
                .build();
    }
}