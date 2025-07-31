package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.MedicalReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMedicalReportService {
    MedicalReportDTO uploadReport(Long patientId, Long doctorId, Long appointmentId, 
                                 MultipartFile file, String fileType, String description, String uploadedBy);
    
    MedicalReportDTO getReportById(Long reportId);
    List<MedicalReportDTO> getPatientReports(Long patientId);
    Page<MedicalReportDTO> getPatientReports(Long patientId, Pageable pageable);
    List<MedicalReportDTO> getReportsByType(Long patientId, String fileType);
    void deleteReport(Long reportId);
    byte[] downloadReport(Long reportId);
}
