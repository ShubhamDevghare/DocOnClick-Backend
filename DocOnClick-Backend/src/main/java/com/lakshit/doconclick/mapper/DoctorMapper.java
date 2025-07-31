package com.lakshit.doconclick.mapper;

import org.springframework.stereotype.Component;

import com.lakshit.doconclick.DTO.DoctorRequestDTO;
import com.lakshit.doconclick.DTO.DoctorResponseDTO;
import com.lakshit.doconclick.DTO.DoctorUpdateDTO;
import com.lakshit.doconclick.entity.Doctor;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequestDTO dto, String profileImageUrl) {
        if (dto == null) {
            return null;
        }

        Doctor doctor = new Doctor();
        doctor.setFullName(dto.getFullName());
        doctor.setMedicalLicenseNumber(dto.getMedicalLicenseNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAddress(dto.getAddress());
        doctor.setGender(dto.getGender());
        doctor.setDateOfBirth(dto.getDateOfBirth());
        doctor.setProfileImage(profileImageUrl != null ? profileImageUrl : "default-profile-image-url");
        doctor.setPassword(dto.getPassword());
        doctor.setRole(dto.getRole());
        doctor.setFees(dto.getFees());
        doctor.setSlotDurationMinutes(dto.getSlotDurationMinutes() != null ? dto.getSlotDurationMinutes() : 30);
        return doctor;
    }

    public DoctorResponseDTO toDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorResponseDTO.builder()
            .doctorId(doctor.getDoctorId())
            .fullName(doctor.getFullName())
            .medicalLicenseNumber(doctor.getMedicalLicenseNumber())
            .specialization(doctor.getSpecialization())
            .experienceYears(doctor.getExperienceYears())
            .email(doctor.getEmail())
            .phone(doctor.getPhone())
            .address(doctor.getAddress())
            .gender(doctor.getGender())
            .dateOfBirth(doctor.getDateOfBirth())
            .profileImage(doctor.getProfileImage())
            .role(doctor.getRole())
            .fees(doctor.getFees())
            .slotDurationMinutes(doctor.getSlotDurationMinutes())
            .isHoliday(doctor.isHoliday())
            .createdAt(doctor.getCreatedAt())
            .updatedAt(doctor.getUpdatedAt())
            .build();
    }

    public void updateEntityFromDTO(DoctorRequestDTO dto, Doctor doctor, String profileImageUrl) {
        if (dto == null || doctor == null) {
            return;
        }

        doctor.setFullName(dto.getFullName());
        doctor.setMedicalLicenseNumber(dto.getMedicalLicenseNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAddress(dto.getAddress());
        doctor.setGender(dto.getGender());
        doctor.setDateOfBirth(dto.getDateOfBirth());

        if (profileImageUrl != null) {
            doctor.setProfileImage(profileImageUrl);
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            doctor.setPassword(dto.getPassword());
        }

        doctor.setRole(dto.getRole());
        doctor.setFees(dto.getFees());
        
        if (dto.getSlotDurationMinutes() != null) {
            doctor.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        }
    }
    
    // New method for updating from DoctorUpdateDTO
    public void updateFromUpdateDTO(DoctorUpdateDTO dto, Doctor doctor, String profileImageUrl) {
        if (dto == null || doctor == null) {
            return;
        }

        doctor.setFullName(dto.getFullName());
        doctor.setMedicalLicenseNumber(dto.getMedicalLicenseNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAddress(dto.getAddress());
        doctor.setGender(dto.getGender());
        doctor.setDateOfBirth(dto.getDateOfBirth());

        if (profileImageUrl != null) {
            doctor.setProfileImage(profileImageUrl);
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            doctor.setPassword(dto.getPassword());
        }

        doctor.setFees(dto.getFees());
        
        if (dto.getSlotDurationMinutes() != null) {
            doctor.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        }
        
        if (dto.getIsHoliday() != null) {
            doctor.setHoliday(dto.getIsHoliday());
        }
    }
}
