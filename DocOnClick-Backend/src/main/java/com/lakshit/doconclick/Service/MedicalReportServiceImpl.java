package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.MedicalReportDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.MedicalReportRepository;
import com.lakshit.doconclick.Repository.PatientRepository;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.MedicalReport;
import com.lakshit.doconclick.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalReportServiceImpl implements IMedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final CloudinaryFileUploadService cloudinaryService;

    @Override
    @Transactional
    public MedicalReportDTO uploadReport(Long patientId, Long doctorId, Long appointmentId,
                                       MultipartFile file, String fileType, String description, String uploadedBy) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = null;
        if (doctorId != null) {
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
        }

        Appointment appointment = null;
        if (appointmentId != null) {
            appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
        }

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new RuntimeException("File size exceeds 10MB limit");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && 
            !contentType.equals("text/plain") && 
            !contentType.startsWith("image/"))) {
            throw new RuntimeException("Only PDF, TXT, and image files are allowed");
        }

        // Upload to Cloudinary using the correct method
        String fileUrl = cloudinaryService.uploadFile(file); // Use the generic method

        MedicalReport report = MedicalReport.builder()
                .patient(patient)
                .doctor(doctor)
                .appointment(appointment)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(fileType)
                .description(description)
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .build();

        MedicalReport savedReport = medicalReportRepository.save(report);
        return mapToDTO(savedReport);
    }

    @Override
    public MedicalReportDTO getReportById(Long reportId) {
        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Medical report not found"));
        return mapToDTO(report);
    }

    @Override
    public List<MedicalReportDTO> getPatientReports(Long patientId) {
        return medicalReportRepository.findByPatientPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MedicalReportDTO> getPatientReports(Long patientId, Pageable pageable) {
        return medicalReportRepository.findByPatientPatientId(patientId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public List<MedicalReportDTO> getReportsByType(Long patientId, String fileType) {
        return medicalReportRepository.findByPatientIdAndFileType(patientId, fileType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Medical report not found"));

        // Delete from Cloudinary using the generic method
        cloudinaryService.deleteFile(report.getFileUrl());
        
        medicalReportRepository.delete(report);
    }

    @Override
    public byte[] downloadReport(Long reportId) {
        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Medical report not found"));

        try {
            URL url = new URL(report.getFileUrl());
            return url.openStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error downloading file", e);
        }
    }

    private MedicalReportDTO mapToDTO(MedicalReport report) {
        return MedicalReportDTO.builder()
                .id(report.getId())
                .patientId(report.getPatient().getPatientId())
                .doctorId(report.getDoctor() != null ? report.getDoctor().getDoctorId() : null)
                .appointmentId(report.getAppointment() != null ? report.getAppointment().getId() : null)
                .fileName(report.getFileName())
                .fileUrl(report.getFileUrl())
                .fileType(report.getFileType())
                .description(report.getDescription())
                .fileSize(report.getFileSize())
                .uploadedAt(report.getUploadedAt())
                .uploadedBy(report.getUploadedBy())
                .patientName(report.getPatient().getFullName())
                .doctorName(report.getDoctor() != null ? report.getDoctor().getFullName() : null)
                .build();
    }
}
