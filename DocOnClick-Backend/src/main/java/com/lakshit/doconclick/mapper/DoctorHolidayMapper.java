package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.DoctorHolidayDTO;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorHoliday;
import org.springframework.stereotype.Component;

@Component
public class DoctorHolidayMapper {

    public DoctorHoliday toEntity(DoctorHolidayDTO dto, Doctor doctor) {
        if (dto == null) {
            return null;
        }

        return DoctorHoliday.builder()
                .id(dto.getId())
                .doctor(doctor)
                .holidayDate(dto.getHolidayDate())
                .reason(dto.getReason())
                .build();
    }

    public DoctorHolidayDTO toDTO(DoctorHoliday entity) {
        if (entity == null) {
            return null;
        }

        return DoctorHolidayDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .holidayDate(entity.getHolidayDate())
                .reason(entity.getReason())
                .build();
    }
}
