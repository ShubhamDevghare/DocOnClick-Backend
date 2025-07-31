package com.lakshit.doconclick.DTO;

import java.time.LocalDate;

import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientResponseDTO {
    private Long patientId;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String phone;  
    private String address;
    private String emailAddress;

    private Role role;
}