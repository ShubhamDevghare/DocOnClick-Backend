package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private Long adminId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String profileImage;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
