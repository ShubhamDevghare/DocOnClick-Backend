package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.PatientAppointmentReportDTO;
import com.lakshit.doconclick.Service.IPatientAppointmentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patient-appointment-reports")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class PatientAppointmentReportController {

    private final IPatientAppointmentReportService reportService;

    @PostMapping("/upload")
    public ResponseEntity<PatientAppointmentReportDTO> uploadReport(
            @RequestParam Long appointmentId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String description) {
        
        PatientAppointmentReportDTO report = reportService.uploadReportForAppointment(
                appointmentId, file, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<PatientAppointmentReportDTO>> getReportsForAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(reportService.getReportsForAppointment(appointmentId));
    }

    @GetMapping("/doctor/{doctorId}/viewable")
    public ResponseEntity<List<PatientAppointmentReportDTO>> getViewableReportsForDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(reportService.getViewableReportsForDoctor(doctorId));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<PatientAppointmentReportDTO> getReport(
            @PathVariable Long reportId,
            @RequestParam Long requestingUserId,
            @RequestParam String userType) {
        return ResponseEntity.ok(reportService.getReportById(reportId, requestingUserId, userType));
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable Long reportId,
            @RequestParam Long requestingUserId,
            @RequestParam String userType) {
        
        byte[] fileData = reportService.downloadReport(reportId, requestingUserId, userType);
        PatientAppointmentReportDTO report = reportService.getReportById(reportId, requestingUserId, userType);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long reportId,
            @RequestParam Long patientId) {
        reportService.deleteReport(reportId, patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reportId}/can-doctor-view")
    public ResponseEntity<Boolean> canDoctorViewReport(
            @PathVariable Long reportId,
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(reportService.canDoctorViewReport(reportId, doctorId));
    }
}
