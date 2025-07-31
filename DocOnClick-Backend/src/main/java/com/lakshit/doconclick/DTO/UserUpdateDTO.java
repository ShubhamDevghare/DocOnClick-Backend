package com.lakshit.doconclick.DTO;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    // Password is optional for updates
    private String password;

    private Gender gender;

    private LocalDate dateOfBirth;

    private MultipartFile profileImage;
}
