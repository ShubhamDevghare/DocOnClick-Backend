package com.lakshit.doconclick.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.DTO.PrescriptionDTO;
import com.lakshit.doconclick.Service.IPrescriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/prescriptions")
//@CrossOrigin(origins = "http://127.0.0.1:5501") 

@RequiredArgsConstructor
public class PrescriptionController {

    private final IPrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO prescriptionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.createPrescription(prescriptionDTO));
    }

    @PutMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable Long prescriptionId,
            @RequestBody PrescriptionDTO prescriptionDTO) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(prescriptionId, prescriptionDTO));
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long prescriptionId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(prescriptionId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByAppointmentId(appointmentId));
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long prescriptionId) {
        prescriptionService.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }
    
    // API to generate prescription PDF
    @GetMapping("/{prescriptionId}/generate-pdf")
    public ResponseEntity<byte[]> generatePrescriptionPDF(@PathVariable Long prescriptionId) {
        // You'll need to implement PDF generation
        // For now, returning empty response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"prescription.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new byte[0]);
    }

    // API to upload prescription file
    @PostMapping("/upload")
    public ResponseEntity<PrescriptionDTO> uploadPrescription(
            @RequestParam Long appointmentId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String notes) {
        // You'll need to implement file upload for prescriptions
        return ResponseEntity.ok(new PrescriptionDTO());
    }

    // API to get patient's prescription history
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<PrescriptionDTO>> getPatientPrescriptionHistory(@PathVariable Long patientId) {
        // You'll need to implement this method
        return ResponseEntity.ok(List.of());
    }

    // API to download prescription
    @GetMapping("/{prescriptionId}/download")
    public ResponseEntity<byte[]> downloadPrescription(@PathVariable Long prescriptionId) {
        // You'll need to implement prescription download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"prescription.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new byte[0]);
    }

}