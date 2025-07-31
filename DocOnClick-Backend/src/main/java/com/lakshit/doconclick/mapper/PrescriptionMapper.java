package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.PrescriptionDTO;
import com.lakshit.doconclick.DTO.PrescriptionMedicineDTO;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Prescription;
import com.lakshit.doconclick.entity.PrescriptionMedicine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrescriptionMapper {

    public Prescription toEntity(PrescriptionDTO dto, Appointment appointment) {
        if (dto == null) {
            return null;
        }

        Prescription prescription = Prescription.builder()
                .id(dto.getId())
                .appointment(appointment)
                .diagnosis(dto.getDiagnosis())
                .notes(dto.getNotes())
                .build();

        // Map medicines
        if (dto.getMedicines() != null) {
            List<PrescriptionMedicine> medicines = dto.getMedicines().stream()
                    .map(medicineDTO -> {
                        PrescriptionMedicine medicine = PrescriptionMedicine.builder()
                                .id(medicineDTO.getId())
                                .medicineName(medicineDTO.getMedicineName())
                                .dosage(medicineDTO.getDosage())
                                .frequency(medicineDTO.getFrequency())
                                .durationDays(medicineDTO.getDurationDays())
                                .instructions(medicineDTO.getInstructions())
                                .build();
                        medicine.setPrescription(prescription);
                        return medicine;
                    })
                    .collect(Collectors.toList());
            prescription.setMedicines(medicines);
        }

        return prescription;
    }

    public PrescriptionDTO toDTO(Prescription entity) {
        if (entity == null) {
            return null;
        }

        PrescriptionDTO dto = PrescriptionDTO.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment().getId())
                .diagnosis(entity.getDiagnosis())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // Map medicines
        if (entity.getMedicines() != null) {
            List<PrescriptionMedicineDTO> medicineDTOs = entity.getMedicines().stream()
                    .map(medicine -> PrescriptionMedicineDTO.builder()
                            .id(medicine.getId())
                            .medicineName(medicine.getMedicineName())
                            .dosage(medicine.getDosage())
                            .frequency(medicine.getFrequency())
                            .durationDays(medicine.getDurationDays())
                            .instructions(medicine.getInstructions())
                            .build())
                    .collect(Collectors.toList());
            dto.setMedicines(medicineDTOs);
        }

        return dto;
    }
}