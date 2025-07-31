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
public class PatientAppointmentReportDTO {
    private Long id;
    private Long appointmentId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String description;
    private LocalDateTime uploadedAt;
    
    // Additional fields for display
    private String patientName;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String appointmentStatus;
}
