package com.lakshit.doconclick.DTO;

import java.util.ArrayList;
import java.util.List;

import com.lakshit.doconclick.enums.CustomDayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyScheduleDTO {
    private Long id;
    private Long doctorId;
    private CustomDayOfWeek dayOfWeek;
    private boolean isAvailable;
    
    @Builder.Default
    private List<WeeklyTimeSlotDTO> timeSlots = new ArrayList<>();
}