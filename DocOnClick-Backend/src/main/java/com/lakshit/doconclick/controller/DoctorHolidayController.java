package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.DoctorHolidayDTO;
import com.lakshit.doconclick.Service.IDoctorHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor-holidays")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class DoctorHolidayController {

    private final IDoctorHolidayService holidayService;

    @PostMapping("/{doctorId}")
    public ResponseEntity<DoctorHolidayDTO> addHoliday(
            @PathVariable Long doctorId,
            @RequestBody DoctorHolidayDTO holidayDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holidayService.addHoliday(doctorId, holidayDTO));
    }

    @PostMapping("/{doctorId}/today")
    public ResponseEntity<DoctorHolidayDTO> markTodayAsHoliday(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holidayService.markTodayAsHoliday(doctorId, reason));
    }

    @DeleteMapping("/{holidayId}")
    public ResponseEntity<Void> removeHoliday(@PathVariable Long holidayId) {
        holidayService.removeHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorHolidayDTO>> getDoctorHolidays(@PathVariable Long doctorId) {
        return ResponseEntity.ok(holidayService.getDoctorHolidays(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/check")
    public ResponseEntity<Boolean> isDoctorOnHoliday(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(holidayService.isDoctorOnHoliday(doctorId, date));
    }
}
