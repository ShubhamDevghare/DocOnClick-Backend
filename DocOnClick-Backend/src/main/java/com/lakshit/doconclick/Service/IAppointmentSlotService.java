package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.AppointmentSlotDTO;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentSlotService {
    void generateAppointmentSlots(Long doctorId, LocalDate date);
    void generateAppointmentSlotsForRange(Long doctorId, LocalDate startDate, LocalDate endDate);
    List<AppointmentSlotDTO> getAvailableSlots(Long doctorId, LocalDate date);
    List<AppointmentSlotDTO> getAvailableSlotsInRange(Long doctorId, LocalDate startDate, LocalDate endDate);
    AppointmentSlotDTO getAppointmentSlot(Long slotId);
    void bookAppointmentSlot(Long slotId);
    void releaseAppointmentSlot(Long slotId);
}