package com.lakshit.doconclick.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lakshit.doconclick.DTO.AppointmentRequestDTO;
import com.lakshit.doconclick.DTO.AppointmentResponseDTO;
import com.lakshit.doconclick.Service.IAppointmentService;
import com.lakshit.doconclick.enums.AppointmentStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
//@CrossOrigin(origins = "http://127.0.0.1:5501") 
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(@Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{appointmentId}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirmAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(appointmentId));
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

    @PutMapping("/{appointmentId}/reject")
    public ResponseEntity<AppointmentResponseDTO> rejectAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(appointmentId));
    }

    // Get paginated & sorted appointments for a User
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> getUserAppointments(
            @PathVariable Long userId, 
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUser(userId, pageable));
    }

    // Get paginated & sorted appointments for a Doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> getDoctorAppointments(
            @PathVariable Long doctorId, 
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId, pageable));
    }
    
    // Get upcoming appointments for a Patient
    @GetMapping("/upcoming")
    public ResponseEntity<Page<AppointmentResponseDTO>> getUpcomingAppointments(
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(userId, pageable));
    }
    
    // Get past appointments for a Patient
    @GetMapping("/past")
    public ResponseEntity<Page<AppointmentResponseDTO>> getPastAppointments(
            @RequestParam Long userId,
            @PageableDefault(size = 8, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getPastAppointments(userId, pageable));
    }
    
    // Get all appointments for a Patient with pagination
    @GetMapping("/all")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAllAppointments(
            @RequestParam Long userId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(userId, pageable));
    } 
    
    // Get appointments by date and status
    @GetMapping("/filter")
    public ResponseEntity<Page<AppointmentResponseDTO>> getFilteredAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getFilteredAppointments(date, status, pageable));
    }
    
    // Get appointments for doctor dashboard - recent appointments
    @GetMapping("/doctor/{doctorId}/recent")
    public ResponseEntity<Page<AppointmentResponseDTO>> getRecentDoctorAppointments(
            @PathVariable Long doctorId,
            @PageableDefault(size = 5, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getRecentDoctorAppointments(doctorId, pageable));
    }
    
    // Get appointments for doctor dashboard - today's appointments
    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<Page<AppointmentResponseDTO>> getTodayDoctorAppointments(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10, sort = "appointmentTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getTodayDoctorAppointments(doctorId, pageable));
    }
    
    // Get appointment history
    @GetMapping("/history")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAppointmentHistory(
            @RequestParam Long userId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAppointmentHistory(userId, pageable));
    }
    
    //get appointment by id and data range
    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<?> getAppointmentsByDoctorAndDateRange(
        @PathVariable Long doctorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppointmentResponseDTO> appointments = 
            appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, startDate, endDate, pageable);
        return ResponseEntity.ok(appointments);
    }
    
    // API to fetch doctor's appointments with filters
    @GetMapping("/doctor/{doctorId}/filtered")
    public ResponseEntity<Page<AppointmentResponseDTO>> getDoctorAppointmentsFiltered(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) AppointmentStatus status,
            @PageableDefault(size = 10) Pageable pageable) {

        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorAndDateRange(
                    doctorId, startDate, endDate, pageable));
        } else if (status != null) {
            // You'll need to add this method to the service
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId, pageable));
        } else {
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId, pageable));
        }
    }

    // API to get upcoming appointments for doctor
    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<Page<AppointmentResponseDTO>> getDoctorUpcomingAppointments(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorAndDateRange(
                doctorId, today, today.plusDays(30), pageable));
    }

    // API to get past appointments for doctor
    @GetMapping("/doctor/{doctorId}/past")
    public ResponseEntity<Page<AppointmentResponseDTO>> getDoctorPastAppointments(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorAndDateRange(
                doctorId, today.minusDays(365), today.minusDays(1), pageable));
    }

    // NEW ENDPOINT: Get appointment history between specific patient and doctor
    @GetMapping("/patient/{patientId}/doctor/{doctorId}/history")
    public ResponseEntity<Page<AppointmentResponseDTO>> getPatientAppointmentHistoryWithDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getPatientAppointmentHistoryWithDoctor(patientId, doctorId, pageable));
    }

    // MISSING ENDPOINTS - Adding these now
    
    // Get all completed appointments for a user
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAllCompletedAppointments(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllCompletedAppointments(userId, pageable));
    }

    // Get all cancelled appointments for a user
    @GetMapping("/user/{userId}/cancelled")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAllCancelledAppointments(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllCancelledAppointments(userId, pageable));
    }

    // Get all pending appointments for a user
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAllPendingAppointments(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAllPendingAppointments(userId, pageable));
    }

    // Search appointments by patient name for specific user
    @GetMapping("/user/{userId}/search/patient")
    public ResponseEntity<Page<AppointmentResponseDTO>> searchAppointmentsByPatientNameForUser(
            @PathVariable Long userId,
            @RequestParam String patientName,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.searchAppointmentsByPatientNameForUser(userId, patientName, pageable));
    }

    // Search appointments by patient name for specific doctor
    @GetMapping("/doctor/{doctorId}/search/patient")
    public ResponseEntity<Page<AppointmentResponseDTO>> searchAppointmentsByPatientNameForDoctor(
            @PathVariable Long doctorId,
            @RequestParam String patientName,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.searchAppointmentsByPatientNameForDoctor(doctorId, patientName, pageable));
    }

    // Search appointments by patient name (general)
    @GetMapping("/search/patient")
    public ResponseEntity<Page<AppointmentResponseDTO>> searchAppointmentsByPatientName(
            @RequestParam String patientName,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.searchAppointmentsByPatientName(patientName, pageable));
    }
}
