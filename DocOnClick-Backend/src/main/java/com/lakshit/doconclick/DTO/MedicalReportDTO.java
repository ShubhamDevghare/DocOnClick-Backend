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
public class MedicalReportDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String description;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private String patientName;
    private String doctorName;
}
