package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Medical license number is required")
    private String medicalLicenseNumber;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years must be non-negative")
    private Integer experienceYears;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private MultipartFile profileImage;

    // Password is optional for updates
    private String password;

    @Min(value = 0, message = "Fees must be non-negative")
    private BigDecimal fees;

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    private Integer slotDurationMinutes;
    
    // New field for holiday status
    private Boolean isHoliday;
}
