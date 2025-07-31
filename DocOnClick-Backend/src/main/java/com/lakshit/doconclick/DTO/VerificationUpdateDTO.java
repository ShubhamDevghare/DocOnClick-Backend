package com.lakshit.doconclick.DTO;

import com.lakshit.doconclick.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationUpdateDTO {
    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;
    private String rejectionReason;
}
