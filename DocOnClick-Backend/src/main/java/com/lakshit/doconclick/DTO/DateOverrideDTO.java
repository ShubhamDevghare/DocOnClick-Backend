package com.lakshit.doconclick.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateOverrideDTO {
    private Long id;
    private Long doctorId;
    private LocalDate overrideDate;
    private boolean isAvailable;
    
    @Builder.Default
    private List<OverrideTimeSlotDTO> timeSlots = new ArrayList<>();
}