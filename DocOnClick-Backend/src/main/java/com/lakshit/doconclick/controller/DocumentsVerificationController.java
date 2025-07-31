package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.DocumentsVerificationDTO;
import com.lakshit.doconclick.DTO.VerificationUpdateDTO;
import com.lakshit.doconclick.Service.IDocumentsVerificationService;
import com.lakshit.doconclick.enums.VerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents-verification")
//@CrossOrigin(origins = "http://127.0.0.1:5502")
@RequiredArgsConstructor
public class DocumentsVerificationController {

    private final IDocumentsVerificationService documentsVerificationService;

    @PostMapping(value = "/upload/{doctorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentsVerificationDTO> uploadDocuments(
            @PathVariable Long doctorId,
            @RequestPart(value = "governmentIdProof", required = false) MultipartFile governmentIdProof,
            @RequestPart(value = "medicalRegistrationCertificate", required = false) MultipartFile medicalRegistrationCertificate,
            @RequestPart(value = "educationalCertificate", required = false) MultipartFile educationalCertificate,
            @RequestPart(value = "experienceCertificate", required = false) MultipartFile experienceCertificate,
            @RequestPart(value = "specializationCertificate", required = false) MultipartFile specializationCertificate) {
        
        DocumentsVerificationDTO result = documentsVerificationService.uploadDocuments(
                doctorId,
                governmentIdProof,
                medicalRegistrationCertificate,
                educationalCertificate,
                experienceCertificate,
                specializationCertificate
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentsVerificationDTO> getDocumentsVerificationById(@PathVariable Long id) {
        return ResponseEntity.ok(documentsVerificationService.getDocumentsVerificationById(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DocumentsVerificationDTO> getDocumentsVerificationByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(documentsVerificationService.getDocumentsVerificationByDoctorId(doctorId));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentsVerificationDTO>> getAllDocumentsVerifications(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentsVerificationService.getAllDocumentsVerifications(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<DocumentsVerificationDTO>> getDocumentsVerificationsByStatus(
            @PathVariable VerificationStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentsVerificationService.getDocumentsVerificationsByStatus(status, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentsVerificationDTO>> searchDocumentsVerificationsByDoctorName(
            @RequestParam String doctorName,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentsVerificationService.searchDocumentsVerificationsByDoctorName(doctorName, pageable));
    }

    @GetMapping("/filter/speciality")
    public ResponseEntity<Page<DocumentsVerificationDTO>> filterDocumentsVerificationsBySpeciality(
            @RequestParam String speciality,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentsVerificationService.filterDocumentsVerificationsBySpeciality(speciality, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<DocumentsVerificationDTO>> filterDocumentsVerificationsByStatusAndSpeciality(
            @RequestParam VerificationStatus status,
            @RequestParam String speciality,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(documentsVerificationService.filterDocumentsVerificationsByStatusAndSpeciality(
                status, speciality, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DocumentsVerificationDTO> updateVerificationStatus(
            @PathVariable Long id,
            @RequestBody VerificationUpdateDTO updateDTO) {
        return ResponseEntity.ok(documentsVerificationService.updateVerificationStatus(id, updateDTO));
    }

    @PutMapping("/doctor/{doctorId}/status")
    public ResponseEntity<DocumentsVerificationDTO> updateVerificationStatusByDoctorId(
            @PathVariable Long doctorId,
            @RequestBody VerificationUpdateDTO updateDTO) {
        return ResponseEntity.ok(documentsVerificationService.updateVerificationStatusByDoctorId(doctorId, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentsVerification(@PathVariable Long id) {
        documentsVerificationService.deleteDocumentsVerification(id);
        return ResponseEntity.noContent().build();
    }
}
