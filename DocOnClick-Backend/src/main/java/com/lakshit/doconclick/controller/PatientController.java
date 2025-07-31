package com.lakshit.doconclick.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lakshit.doconclick.DTO.PatientDetailDTO;
import com.lakshit.doconclick.DTO.PatientRequestDTO;
import com.lakshit.doconclick.DTO.PatientResponseDTO;
import com.lakshit.doconclick.Service.IPatientService;

@RestController
@RequestMapping("/api/v1/patients")
//@CrossOrigin(origins = "http://127.0.0.1:5501") 
public class PatientController {
    
    @Autowired
    private IPatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<PatientResponseDTO> register(@RequestBody PatientRequestDTO patientDTO) {
        return ResponseEntity.ok(patientService.registerPatient(patientDTO));
    }

    // New endpoint for find or create patient
//    @PostMapping("/find-or-create")
//    public ResponseEntity<PatientResponseDTO> findOrCreatePatient(@RequestBody PatientRequestDTO patientDTO) {
//        return ResponseEntity.ok(patientService.findOrCreatePatient(patientDTO));
//    }

    // New endpoint to check if patient exists
    @PostMapping("/check-existing")
    public ResponseEntity<PatientResponseDTO> checkExistingPatient(@RequestBody PatientRequestDTO patientDTO) {
        return patientService.findExistingPatient(patientDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{patientId}/complete-details")
    public ResponseEntity<PatientDetailDTO> getCompletePatientDetails(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getCompletePatientDetails(patientId));
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<PatientResponseDTO>> searchPatientsByName(@RequestParam String name) {
        return ResponseEntity.ok(patientService.searchPatientsByName(name));
    }

    @GetMapping("/search/by-phone")
    public ResponseEntity<PatientResponseDTO> searchPatientByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(patientService.searchPatientByPhone(phone));
    }

    @GetMapping("/filter/by-appointment-date")
    public ResponseEntity<Page<PatientResponseDTO>> filterPatientsByAppointmentDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(patientService.filterPatientsByAppointmentDate(appointmentDate, pageable));
    }

    @GetMapping("/search/by-partial-phone")
    public ResponseEntity<List<PatientResponseDTO>> searchPatientsByPartialPhone(@RequestParam String phonePartial) {
        return ResponseEntity.ok(patientService.searchPatientsByPartialPhone(phonePartial));
    }
}
