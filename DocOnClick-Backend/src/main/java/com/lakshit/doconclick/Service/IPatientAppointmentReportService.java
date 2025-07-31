package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PatientAppointmentReportDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPatientAppointmentReportService {
    
    // Upload report during appointment booking
    PatientAppointmentReportDTO uploadReportForAppointment(
            Long appointmentId, 
            MultipartFile file, 
            String description
    );
    
    // Get reports for an appointment (for patient)
    List<PatientAppointmentReportDTO> getReportsForAppointment(Long appointmentId);
    
    // Get reports that a doctor can view
    List<PatientAppointmentReportDTO> getViewableReportsForDoctor(Long doctorId);
    
    // Get specific report (with access control)
    PatientAppointmentReportDTO getReportById(Long reportId, Long requestingUserId, String userType);
    
    // Delete report (only by patient who uploaded it)
    void deleteReport(Long reportId, Long patientId);
    
    // Download report file
    byte[] downloadReport(Long reportId, Long requestingUserId, String userType);
    
    // Check if doctor can view specific report
    boolean canDoctorViewReport(Long reportId, Long doctorId);
}
