package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {

    private Long doctorId;
    private String fullName;
    private String medicalLicenseNumber;
    private String specialization;
    private Integer experienceYears;
    private String email;
    private String phone;
    private String address;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String profileImage;
    private Role role;
    private BigDecimal fees;
    private Integer slotDurationMinutes;
    private boolean isHoliday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
