package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.WeeklyScheduleDTO;
import com.lakshit.doconclick.enums.CustomDayOfWeek;

import java.util.List;

public interface IWeeklyScheduleService {
    WeeklyScheduleDTO createOrUpdateWeeklySchedule(Long doctorId, WeeklyScheduleDTO scheduleDTO);
    WeeklyScheduleDTO getWeeklySchedule(Long scheduleId);
    WeeklyScheduleDTO getWeeklyScheduleByDoctorAndDay(Long doctorId, CustomDayOfWeek dayOfWeek);
    List<WeeklyScheduleDTO> getAllWeeklySchedulesByDoctor(Long doctorId);
    void deleteWeeklySchedule(Long scheduleId);
    void setDayAvailability(Long doctorId, CustomDayOfWeek dayOfWeek, boolean isAvailable);
    WeeklyScheduleDTO toggleDayAvailability(Long doctorId, CustomDayOfWeek dayOfWeek);
}