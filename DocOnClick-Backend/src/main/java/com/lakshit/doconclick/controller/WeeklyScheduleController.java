package com.lakshit.doconclick.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lakshit.doconclick.DTO.WeeklyScheduleDTO;
import com.lakshit.doconclick.Service.IWeeklyScheduleService;
import com.lakshit.doconclick.enums.CustomDayOfWeek;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/weekly-schedules")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class WeeklyScheduleController {

    private final IWeeklyScheduleService weeklyScheduleService;

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<WeeklyScheduleDTO> createOrUpdateWeeklySchedule(
            @PathVariable Long doctorId,
            @RequestBody WeeklyScheduleDTO scheduleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weeklyScheduleService.createOrUpdateWeeklySchedule(doctorId, scheduleDTO));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<WeeklyScheduleDTO> getWeeklySchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(weeklyScheduleService.getWeeklySchedule(scheduleId));
    }

    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<WeeklyScheduleDTO> getWeeklyScheduleByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable CustomDayOfWeek dayOfWeek) {
        return ResponseEntity.ok(weeklyScheduleService.getWeeklyScheduleByDoctorAndDay(doctorId, dayOfWeek));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<WeeklyScheduleDTO>> getAllWeeklySchedulesByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(weeklyScheduleService.getAllWeeklySchedulesByDoctor(doctorId));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteWeeklySchedule(@PathVariable Long scheduleId) {
        weeklyScheduleService.deleteWeeklySchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/doctor/{doctorId}/day/{dayOfWeek}/availability")
    public ResponseEntity<Void> setDayAvailability(
            @PathVariable Long doctorId,
            @PathVariable CustomDayOfWeek dayOfWeek,
            @RequestParam boolean isAvailable) {
        weeklyScheduleService.setDayAvailability(doctorId, dayOfWeek, isAvailable);
        return ResponseEntity.ok().build();
    }
    
    // New endpoint for toggling availability
    @PostMapping("/doctor/{doctorId}/day/{dayOfWeek}/toggle")
    public ResponseEntity<WeeklyScheduleDTO> toggleDayAvailability(
            @PathVariable Long doctorId,
            @PathVariable CustomDayOfWeek dayOfWeek) {
        return ResponseEntity.ok(weeklyScheduleService.toggleDayAvailability(doctorId, dayOfWeek));
    }
}