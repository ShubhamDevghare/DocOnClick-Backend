package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.WeeklyScheduleDTO;
import com.lakshit.doconclick.DTO.WeeklyTimeSlotDTO;
import com.lakshit.doconclick.entity.DoctorWeeklySchedule;
import com.lakshit.doconclick.entity.WeeklyTimeSlot;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeeklyScheduleMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public WeeklyScheduleDTO toDTO(DoctorWeeklySchedule entity, List<WeeklyTimeSlot> timeSlots) {
        if (entity == null) {
            return null;
        }

        List<WeeklyTimeSlotDTO> timeSlotDTOs = timeSlots.stream()
                .map(slot -> WeeklyTimeSlotDTO.builder()
                        .id(slot.getId())
                        .startTime(slot.getStartTime().format(TIME_FORMATTER))
                        .endTime(slot.getEndTime().format(TIME_FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return WeeklyScheduleDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .dayOfWeek(entity.getDayOfWeek())
                .isAvailable(entity.isAvailable())
                .timeSlots(timeSlotDTOs)
                .build();
    }
}