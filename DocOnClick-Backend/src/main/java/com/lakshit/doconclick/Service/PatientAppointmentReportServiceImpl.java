package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PatientAppointmentReportDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.PatientAppointmentReportRepository;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.PatientAppointmentReport;
import com.lakshit.doconclick.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientAppointmentReportServiceImpl implements IPatientAppointmentReportService {

    private final PatientAppointmentReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;
    private final CloudinaryFileUploadService cloudinaryService;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/jpg", "image/png", "text/plain"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    @Transactional
    public PatientAppointmentReportDTO uploadReportForAppointment(
            Long appointmentId, 
            MultipartFile file, 
            String description) {
        
        // Validate appointment exists and is in correct state
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Only allow upload during booking (PENDING status) or before confirmation
        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED ||
            appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot upload reports for completed or cancelled appointments");
        }

        // Validate file
        validateFile(file);

        // Upload to Cloudinary using the correct method
        String fileUrl = cloudinaryService.uploadFile(file); // Use the generic method

        // Create report entity
        PatientAppointmentReport report = PatientAppointmentReport.builder()
                .appointment(appointment)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .description(description)
                .build();

        PatientAppointmentReport savedReport = reportRepository.save(report);
        return mapToDTO(savedReport);
    }

    @Override
    public List<PatientAppointmentReportDTO> getReportsForAppointment(Long appointmentId) {
        return reportRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientAppointmentReportDTO> getViewableReportsForDoctor(Long doctorId) {
        return reportRepository.findViewableReportsByDoctor(doctorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientAppointmentReportDTO getReportById(Long reportId, Long requestingUserId, String userType) {
        PatientAppointmentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Access control
        if (!canUserAccessReport(report, requestingUserId, userType)) {
            throw new RuntimeException("Access denied to this report");
        }

        return mapToDTO(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId, Long patientId) {
        PatientAppointmentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Only patient who uploaded can delete
        if (!report.getPatient().getPatientId().equals(patientId)) {
            throw new RuntimeException("Only the patient who uploaded the report can delete it");
        }

        // Delete from Cloudinary using the generic method
        cloudinaryService.deleteFile(report.getFileUrl());
        
        // Delete from database
        reportRepository.delete(report);
    }

    @Override
    public byte[] downloadReport(Long reportId, Long requestingUserId, String userType) {
        PatientAppointmentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Access control
        if (!canUserAccessReport(report, requestingUserId, userType)) {
            throw new RuntimeException("Access denied to this report");
        }

        try {
            URL url = new URL(report.getFileUrl());
            return url.openStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error downloading file", e);
        }
    }

    @Override
    public boolean canDoctorViewReport(Long reportId, Long doctorId) {
        return reportRepository.canDoctorViewReport(reportId, doctorId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 10MB limit");
        }

        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("File type not allowed. Supported types: PDF, JPG, PNG, TXT");
        }
    }

    private boolean canUserAccessReport(PatientAppointmentReport report, Long requestingUserId, String userType) {
        switch (userType.toUpperCase()) {
            case "PATIENT":
                return report.getPatient().getPatientId().equals(requestingUserId);
            case "DOCTOR":
                return canDoctorViewReport(report.getId(), requestingUserId);
            case "ADMIN":
                return true; // Admins can view all reports
            default:
                return false;
        }
    }

    private PatientAppointmentReportDTO mapToDTO(PatientAppointmentReport report) {
        return PatientAppointmentReportDTO.builder()
                .id(report.getId())
                .appointmentId(report.getAppointment().getId())
                .fileName(report.getFileName())
                .fileUrl(report.getFileUrl())
                .fileType(report.getFileType())
                .fileSize(report.getFileSize())
                .description(report.getDescription())
                .uploadedAt(report.getUploadedAt())
                .patientName(report.getPatient().getFullName())
                .doctorName(report.getDoctor().getFullName())
                .appointmentDate(report.getAppointment().getAppointmentDate().toString())
                .appointmentTime(report.getAppointment().getAppointmentTime().toString())
                .appointmentStatus(report.getAppointment().getAppointmentStatus().toString())
                .build();
    }
}
