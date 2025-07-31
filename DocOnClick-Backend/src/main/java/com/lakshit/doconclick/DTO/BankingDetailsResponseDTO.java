package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankingDetailsResponseDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private String bankName;
    private String accountNumber;
    private String cifNumber;
    private String upiId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
