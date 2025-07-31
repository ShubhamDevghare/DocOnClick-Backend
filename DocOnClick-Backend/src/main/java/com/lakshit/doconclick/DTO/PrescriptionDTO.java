package com.lakshit.doconclick.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private Long id;
    private Long appointmentId;
    private String diagnosis;
    private String notes;
    private List<PrescriptionMedicineDTO> medicines = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}