package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.AppointmentSlotDTO;
import com.lakshit.doconclick.entity.AppointmentSlot;
import org.springframework.stereotype.Component;

@Component
public class AppointmentSlotMapper {

    public AppointmentSlotDTO toDTO(AppointmentSlot entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentSlotDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .isBooked(entity.isBooked())
                .build();
    }
}