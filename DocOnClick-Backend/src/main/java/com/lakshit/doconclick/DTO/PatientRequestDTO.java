package com.lakshit.doconclick.DTO;

import java.time.LocalDate;
import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientRequestDTO {
 private String fullName;
 private Gender gender;
 private LocalDate dateOfBirth;
 private String phone;
 private String emailAddress;
 private String address;
 private Role role;
}