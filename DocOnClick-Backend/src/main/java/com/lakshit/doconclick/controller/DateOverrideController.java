package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.DateOverrideDTO;
import com.lakshit.doconclick.Service.IDateOverrideService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/date-overrides")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class DateOverrideController {

    private final IDateOverrideService dateOverrideService;

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<DateOverrideDTO> createOrUpdateDateOverride(
            @PathVariable Long doctorId,
            @RequestBody DateOverrideDTO overrideDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dateOverrideService.createOrUpdateDateOverride(doctorId, overrideDTO));
    }

    @GetMapping("/{overrideId}")
    public ResponseEntity<DateOverrideDTO> getDateOverride(@PathVariable Long overrideId) {
        return ResponseEntity.ok(dateOverrideService.getDateOverride(overrideId));
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<DateOverrideDTO> getDateOverrideByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(dateOverrideService.getDateOverrideByDoctorAndDate(doctorId, date));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DateOverrideDTO>> getDateOverridesByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(dateOverrideService.getDateOverridesByDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<List<DateOverrideDTO>> getDateOverridesInRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dateOverrideService.getDateOverridesInRange(doctorId, startDate, endDate));
    }

    @DeleteMapping("/{overrideId}")
    public ResponseEntity<Void> deleteDateOverride(@PathVariable Long overrideId) {
        dateOverrideService.deleteDateOverride(overrideId);
        return ResponseEntity.noContent().build();
    }
}