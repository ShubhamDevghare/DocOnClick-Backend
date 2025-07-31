package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.MedicalReportDTO;
import com.lakshit.doconclick.Service.IMedicalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-reports")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class MedicalReportController {

    private final IMedicalReportService medicalReportService;

    @PostMapping("/upload")
    public ResponseEntity<MedicalReportDTO> uploadReport(
            @RequestParam Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam MultipartFile file,
            @RequestParam String fileType,
            @RequestParam(required = false) String description,
            @RequestParam String uploadedBy) {
        
        MedicalReportDTO report = medicalReportService.uploadReport(
                patientId, doctorId, appointmentId, file, fileType, description, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<MedicalReportDTO> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(medicalReportService.getReportById(reportId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalReportDTO>> getPatientReports(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalReportService.getPatientReports(patientId));
    }

    @GetMapping("/patient/{patientId}/paginated")
    public ResponseEntity<Page<MedicalReportDTO>> getPatientReportsPaginated(
            @PathVariable Long patientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(medicalReportService.getPatientReports(patientId, pageable));
    }

    @GetMapping("/patient/{patientId}/type/{fileType}")
    public ResponseEntity<List<MedicalReportDTO>> getReportsByType(
            @PathVariable Long patientId,
            @PathVariable String fileType) {
        return ResponseEntity.ok(medicalReportService.getReportsByType(patientId, fileType));
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long reportId) {
        byte[] fileData = medicalReportService.downloadReport(reportId);
        MedicalReportDTO report = medicalReportService.getReportById(reportId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        medicalReportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
