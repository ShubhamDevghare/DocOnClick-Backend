package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetailDTO {
    private Long patientId;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String emailAddress;
    private String address;
    private List<AppointmentResponseDTO> appointments;
    private List<PrescriptionDTO> prescriptions;
    private List<MedicalReportDTO> medicalReports;
}
