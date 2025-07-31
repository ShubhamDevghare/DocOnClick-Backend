package com.lakshit.doconclick.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long userId;

    private String fullName;

    private String email;

    private String phone;

    private String address;

    private Role role;

    private Gender gender;

    private LocalDate dateOfBirth;

    @Builder.Default
    private String profileImage = "default-profile-image-url";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
