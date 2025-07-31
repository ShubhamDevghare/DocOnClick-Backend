package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.AppointmentSlotDTO;
import com.lakshit.doconclick.Service.IAppointmentSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment-slots")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class AppointmentSlotController {

    private final IAppointmentSlotService appointmentSlotService;

    @PostMapping("/generate/doctor/{doctorId}/date/{date}")
    public ResponseEntity<Void> generateAppointmentSlots(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        appointmentSlotService.generateAppointmentSlots(doctorId, date);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/doctor/{doctorId}/range")
    public ResponseEntity<Void> generateAppointmentSlotsForRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        appointmentSlotService.generateAppointmentSlotsForRange(doctorId, startDate, endDate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<AppointmentSlotDTO>> getAvailableSlots(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentSlotService.getAvailableSlots(doctorId, date));
    }

    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<List<AppointmentSlotDTO>> getAvailableSlotsInRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(appointmentSlotService.getAvailableSlotsInRange(doctorId, startDate, endDate));
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<AppointmentSlotDTO> getAppointmentSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(appointmentSlotService.getAppointmentSlot(slotId));
    }

    @PostMapping("/{slotId}/book")
    public ResponseEntity<Void> bookAppointmentSlot(@PathVariable Long slotId) {
        appointmentSlotService.bookAppointmentSlot(slotId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{slotId}/release")
    public ResponseEntity<Void> releaseAppointmentSlot(@PathVariable Long slotId) {
        appointmentSlotService.releaseAppointmentSlot(slotId);
        return ResponseEntity.ok().build();
    }
}