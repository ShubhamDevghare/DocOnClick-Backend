package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.DateOverrideDTO;
import com.lakshit.doconclick.DTO.OverrideTimeSlotDTO;
import com.lakshit.doconclick.entity.DateOverride;
import com.lakshit.doconclick.entity.OverrideTimeSlot;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DateOverrideMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public DateOverrideDTO toDTO(DateOverride entity, List<OverrideTimeSlot> timeSlots) {
        if (entity == null) {
            return null;
        }

        List<OverrideTimeSlotDTO> timeSlotDTOs = timeSlots.stream()
                .map(slot -> OverrideTimeSlotDTO.builder()
                        .id(slot.getId())
                        .startTime(slot.getStartTime().format(TIME_FORMATTER))
                        .endTime(slot.getEndTime().format(TIME_FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return DateOverrideDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .overrideDate(entity.getOverrideDate())
                .isAvailable(entity.isAvailable())
                .timeSlots(timeSlotDTOs)
                .build();
    }
}