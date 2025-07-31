package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotDTO {
    private Long id;
    private Long doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBooked;
}