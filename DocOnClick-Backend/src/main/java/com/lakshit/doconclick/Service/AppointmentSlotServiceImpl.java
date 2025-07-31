package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.AppointmentSlotDTO;
import com.lakshit.doconclick.entity.*;
import com.lakshit.doconclick.enums.CustomDayOfWeek;
import com.lakshit.doconclick.exception.ResourceNotFoundException;
import com.lakshit.doconclick.mapper.AppointmentSlotMapper;
import com.lakshit.doconclick.Repository.*;
import com.lakshit.doconclick.Service.IAppointmentSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentSlotServiceImpl implements IAppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorWeeklyScheduleRepository weeklyScheduleRepository;
    private final WeeklyTimeSlotRepository weeklyTimeSlotRepository;
    private final DateOverrideRepository dateOverrideRepository;
    private final OverrideTimeSlotRepository overrideTimeSlotRepository;
    private final DoctorHolidayRepository holidayRepository;
    private final AppointmentSlotMapper appointmentSlotMapper;

    @Override
    @Transactional
    public void generateAppointmentSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // Check if the date is a holiday
        if (holidayRepository.existsByDoctorDoctorIdAndHolidayDate(doctorId, date)) {
            // Delete any existing non-booked slots for this date
            appointmentSlotRepository.deleteByDoctorAndDateAndIsBookedFalse(doctor, date);
            return;
        }

        // Check if there's a date override
        Optional<DateOverride> overrideOpt = dateOverrideRepository.findByDoctorAndOverrideDate(doctor, date);
        
        if (overrideOpt.isPresent()) {
            DateOverride override = overrideOpt.get();
            
            // Delete any existing non-booked slots for this date
            appointmentSlotRepository.deleteByDoctorAndDateAndIsBookedFalse(doctor, date);
            
            if (!override.isAvailable()) return;
            
            // Generate slots based on override time slots
            List<OverrideTimeSlot> overrideTimeSlots = overrideTimeSlotRepository.findByDateOverride(override);
            for (OverrideTimeSlot timeSlot : overrideTimeSlots) {
                generateSlotsForTimeRange(doctor, date, timeSlot.getStartTime(), timeSlot.getEndTime());
            }
            
            return;
        }
        
        // No override, use weekly schedule
        CustomDayOfWeek dayOfWeek = convertToDayOfWeek(date);
        Optional<DoctorWeeklySchedule> scheduleOpt = weeklyScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
        
        if (scheduleOpt.isPresent()) {
            DoctorWeeklySchedule schedule = scheduleOpt.get();
            
            // Delete any existing non-booked slots for this date
            appointmentSlotRepository.deleteByDoctorAndDateAndIsBookedFalse(doctor, date);
            
            if (!schedule.isAvailable()) return;
            
            // Generate slots based on weekly time slots
            List<WeeklyTimeSlot> weeklyTimeSlots = weeklyTimeSlotRepository.findByWeeklySchedule(schedule);
            for (WeeklyTimeSlot timeSlot : weeklyTimeSlots) {
                generateSlotsForTimeRange(doctor, date, timeSlot.getStartTime(), timeSlot.getEndTime());
            }
        }
    }

    @Override
    @Transactional
    public void generateAppointmentSlotsForRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            generateAppointmentSlots(doctorId, date);
        }
    }

    private void generateSlotsForTimeRange(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime) {
        int slotDurationMinutes = doctor.getSlotDurationMinutes();

        LocalTime currentTime = startTime;
        while (currentTime.plus(slotDurationMinutes, ChronoUnit.MINUTES).compareTo(endTime) <= 0) {
            LocalTime slotEndTime = currentTime.plusMinutes(slotDurationMinutes);

            boolean slotExists = appointmentSlotRepository.existsByDoctorAndDateAndStartTimeAndEndTimeAndIsBookedTrue(
                    doctor, date, currentTime, slotEndTime);

            if (!slotExists) {
                AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                        .doctor(doctor)
                        .date(date)
                        .startTime(currentTime)
                        .endTime(slotEndTime)
                        .isBooked(false)
                        .build();

                appointmentSlotRepository.save(appointmentSlot);
            }

            currentTime = slotEndTime;
        }
    }

    @Override
    public List<AppointmentSlotDTO> getAvailableSlots(Long doctorId, LocalDate date) {
        List<AppointmentSlot> slots = appointmentSlotRepository.findByDoctorDoctorIdAndDateAndIsBookedFalse(doctorId, date);
        return slots.stream()
                .map(appointmentSlotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentSlotDTO> getAvailableSlotsInRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        List<AppointmentSlot> slots = appointmentSlotRepository.findByDoctorDoctorIdAndDateBetweenAndIsBookedFalse(
                doctorId, startDate, endDate);
        return slots.stream()
                .map(appointmentSlotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentSlotDTO getAppointmentSlot(Long slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with id: " + slotId));
        return appointmentSlotMapper.toDTO(slot);
    }

    @Override
    @Transactional
    public void bookAppointmentSlot(Long slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with id: " + slotId));
        
        if (slot.isBooked()) {
            throw new IllegalStateException("Appointment slot is already booked");
        }
        
        slot.setBooked(true);
        appointmentSlotRepository.save(slot);
    }

    @Override
    @Transactional
    public void releaseAppointmentSlot(Long slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with id: " + slotId));
        
        if (!slot.isBooked()) {
            throw new IllegalStateException("Appointment slot is not booked");
        }
        
        slot.setBooked(false);
        appointmentSlotRepository.save(slot);
    }

    private CustomDayOfWeek convertToDayOfWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY: return CustomDayOfWeek.MONDAY;
            case TUESDAY: return CustomDayOfWeek.TUESDAY;
            case WEDNESDAY: return CustomDayOfWeek.WEDNESDAY;
            case THURSDAY: return CustomDayOfWeek.THURSDAY;
            case FRIDAY: return CustomDayOfWeek.FRIDAY;
            case SATURDAY: return CustomDayOfWeek.SATURDAY;
            case SUNDAY: return CustomDayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }
}