package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.DoctorHolidayDTO;

import java.time.LocalDate;
import java.util.List;

public interface IDoctorHolidayService {
    DoctorHolidayDTO addHoliday(Long doctorId, DoctorHolidayDTO holidayDTO);
    DoctorHolidayDTO markTodayAsHoliday(Long doctorId, String reason);
    void removeHoliday(Long holidayId);
    List<DoctorHolidayDTO> getDoctorHolidays(Long doctorId);
    boolean isDoctorOnHoliday(Long doctorId, LocalDate date);
}
