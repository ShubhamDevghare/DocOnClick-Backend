package com.lakshit.doconclick.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
