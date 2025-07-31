package com.lakshit.doconclick.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.DTO.DoctorLoginDTO;
import com.lakshit.doconclick.DTO.DoctorPasswordChangeDTO;
import com.lakshit.doconclick.DTO.DoctorRequestDTO;
import com.lakshit.doconclick.DTO.DoctorResponseDTO;
import com.lakshit.doconclick.DTO.DoctorStatsDTO;
import com.lakshit.doconclick.DTO.DoctorUpdateDTO;
import com.lakshit.doconclick.Service.IAppointmentSlotService;
import com.lakshit.doconclick.Service.IDoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/doctors")
//@CrossOrigin(origins = "http://127.0.0.1:5501") 
@RequiredArgsConstructor
public class DoctorController {
    private final IAppointmentSlotService appointmentSlotService;

    private final IDoctorService doctorService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorResponseDTO> signUp(
            @RequestPart("userData") DoctorRequestDTO doctorRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            doctorRequestDTO.setProfileImage(profileImage);
        }
        
        return new ResponseEntity<>(doctorService.signUp(doctorRequestDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<DoctorResponseDTO> login(@RequestBody @Validated DoctorLoginDTO loginDTO) {
        return ResponseEntity.ok(doctorService.login(loginDTO.getEmail(), loginDTO.getPassword()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDTO>> getAllDoctors(
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long id,
            @RequestPart("doctorData") @Valid DoctorUpdateDTO doctorUpdateDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            doctorUpdateDTO.setProfileImage(profileImage);
        }
        
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorUpdateDTO));
    }

    @PatchMapping("/{doctorId}/slot-duration")
    public ResponseEntity<DoctorResponseDTO> updateSlotDuration(
            @PathVariable Long doctorId,
            @RequestParam Integer durationMinutes) {
        
        // Update the doctor's slot duration
        DoctorResponseDTO updatedDoctor = doctorService.updateSlotDuration(doctorId, durationMinutes);
        
        // Regenerate all future appointment slots with the new duration
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30); // Generate for the next 30 days
        appointmentSlotService.generateAppointmentSlotsForRange(doctorId, today, endDate);
        
        return ResponseEntity.ok(updatedDoctor);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<DoctorResponseDTO>> searchDoctors(
            @RequestParam(required = false) String speciality,
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(doctorService.searchDoctorsBySpeciality(speciality, pageable));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<DoctorResponseDTO>> filterDoctors(
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String location,
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(doctorService.filterDoctors(speciality, minRating, location, pageable));
    }
    
    @GetMapping("/sort")
    public ResponseEntity<Page<DoctorResponseDTO>> sortDoctors(
            @RequestParam String by, // rating or experience
            @RequestParam String order, // asc or desc
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(doctorService.sortDoctors(by, order, pageable));
    }
    
    @GetMapping("/combined")
    public ResponseEntity<Page<DoctorResponseDTO>> combinedSearch(
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(doctorService.combinedSearch(speciality, minRating, sort, order, pageable));
    }
    
    @GetMapping("/specialities")
    public ResponseEntity<List<String>> getAllSpecialities() {
        return ResponseEntity.ok(doctorService.getAllSpecialities());
    }
    
    @PostMapping("/{id}/holidays")
    public ResponseEntity<DoctorResponseDTO> markTodayAsHoliday(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.markTodayAsHoliday(id));
    }
    
    @PostMapping("/{id}/holidays/date")
    public ResponseEntity<DoctorResponseDTO> markDateAsHoliday(
            @PathVariable Long id,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) 
            java.time.LocalDate date) {
        return ResponseEntity.ok(doctorService.markDateAsHoliday(id, date));
    }
    
    @GetMapping("/{doctorId}/stats")
    public ResponseEntity<DoctorStatsDTO> getDoctorStats(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorStats(doctorId));
    }
    
    @PutMapping("/{doctorId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long doctorId,
            @RequestBody DoctorPasswordChangeDTO passwordChangeDTO) {
        doctorService.changePassword(doctorId, passwordChangeDTO);
        return ResponseEntity.ok().build();
    }
}
