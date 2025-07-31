package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionMedicineDTO {
    private Long id;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private String instructions;
}