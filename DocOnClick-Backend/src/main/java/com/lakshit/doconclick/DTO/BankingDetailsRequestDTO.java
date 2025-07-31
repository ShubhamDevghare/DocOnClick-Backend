package com.lakshit.doconclick.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankingDetailsRequestDTO {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotBlank(message = "Bank name is required")
    @Size(max = 255, message = "Bank name must not exceed 255 characters")
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
    private String accountNumber;

    @Size(max = 50, message = "CIF number must not exceed 50 characters")
    private String cifNumber;

    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$", 
             message = "Invalid UPI ID format")
    @Size(max = 100, message = "UPI ID must not exceed 100 characters")
    private String upiId;
}
