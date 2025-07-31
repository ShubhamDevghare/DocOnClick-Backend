package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.WeeklyScheduleDTO;
import com.lakshit.doconclick.DTO.WeeklyTimeSlotDTO;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorWeeklySchedule;
import com.lakshit.doconclick.entity.WeeklyTimeSlot;
import com.lakshit.doconclick.enums.CustomDayOfWeek;
import com.lakshit.doconclick.exception.ResourceNotFoundException;
import com.lakshit.doconclick.mapper.WeeklyScheduleMapper;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.DoctorWeeklyScheduleRepository;
import com.lakshit.doconclick.Repository.WeeklyTimeSlotRepository;
import com.lakshit.doconclick.Service.IAppointmentSlotService;
import com.lakshit.doconclick.Service.IWeeklyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WeeklyScheduleServiceImpl implements IWeeklyScheduleService {

    private final DoctorWeeklyScheduleRepository weeklyScheduleRepository;
    private final WeeklyTimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final WeeklyScheduleMapper weeklyScheduleMapper;
    private final IAppointmentSlotService appointmentSlotService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    @Transactional
    public WeeklyScheduleDTO createOrUpdateWeeklySchedule(Long doctorId, WeeklyScheduleDTO scheduleDTO) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        DoctorWeeklySchedule schedule;
        boolean isNew = false;

        // Check if schedule already exists for this day
        if (scheduleDTO.getId() != null) {
            schedule = weeklyScheduleRepository.findById(scheduleDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Weekly schedule not found with id: " + scheduleDTO.getId()));
        } else {
            // Try to find by doctor and day of week
            schedule = weeklyScheduleRepository.findByDoctorAndDayOfWeek(doctor, scheduleDTO.getDayOfWeek())
                    .orElse(null);

            if (schedule == null) {
                schedule = new DoctorWeeklySchedule();
                schedule.setDoctor(doctor);
                schedule.setDayOfWeek(scheduleDTO.getDayOfWeek());
                isNew = true;
            }
        }

        // Set availability from DTO
        schedule.setAvailable(scheduleDTO.isAvailable());
        DoctorWeeklySchedule savedSchedule = weeklyScheduleRepository.save(schedule);

        // Handle time slots
        List<WeeklyTimeSlot> newTimeSlots = new ArrayList<>();
        
        // Delete existing time slots if any
        if (!isNew) {
            List<WeeklyTimeSlot> existingSlots = timeSlotRepository.findByWeeklySchedule(savedSchedule);
            timeSlotRepository.deleteAll(existingSlots);
        }
        
        // Create new time slots if available and time slots are provided
        if (scheduleDTO.getTimeSlots() != null && !scheduleDTO.getTimeSlots().isEmpty()) {
            for (WeeklyTimeSlotDTO slotDTO : scheduleDTO.getTimeSlots()) {
                WeeklyTimeSlot timeSlot = new WeeklyTimeSlot();
                timeSlot.setStartTime(LocalTime.parse(slotDTO.getStartTime()));
                timeSlot.setEndTime(LocalTime.parse(slotDTO.getEndTime()));
                timeSlot.setWeeklySchedule(savedSchedule);
                newTimeSlots.add(timeSlotRepository.save(timeSlot));
            }
        }

        // Regenerate appointment slots for upcoming dates with this day of week
        regenerateAppointmentSlotsForDay(doctorId, scheduleDTO.getDayOfWeek());

        // Return the updated schedule with time slots
        return weeklyScheduleMapper.toDTO(savedSchedule, newTimeSlots);
    }

    @Override
    public WeeklyScheduleDTO getWeeklySchedule(Long scheduleId) {
        DoctorWeeklySchedule schedule = weeklyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly schedule not found with id: " + scheduleId));

        List<WeeklyTimeSlot> timeSlots = timeSlotRepository.findByWeeklySchedule(schedule);
        return weeklyScheduleMapper.toDTO(schedule, timeSlots);
    }

    @Override
    public WeeklyScheduleDTO getWeeklyScheduleByDoctorAndDay(Long doctorId, CustomDayOfWeek dayOfWeek) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        DoctorWeeklySchedule schedule = weeklyScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            // Create a default schedule if none exists
            schedule = new DoctorWeeklySchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(dayOfWeek);
            schedule.setAvailable(true);
            schedule = weeklyScheduleRepository.save(schedule);
        }

        List<WeeklyTimeSlot> timeSlots = timeSlotRepository.findByWeeklySchedule(schedule);
        return weeklyScheduleMapper.toDTO(schedule, timeSlots);
    }

    @Override
    public List<WeeklyScheduleDTO> getAllWeeklySchedulesByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        List<DoctorWeeklySchedule> schedules = weeklyScheduleRepository.findByDoctor(doctor);
        
        return schedules.stream()
                .map(schedule -> {
                    List<WeeklyTimeSlot> timeSlots = timeSlotRepository.findByWeeklySchedule(schedule);
                    return weeklyScheduleMapper.toDTO(schedule, timeSlots);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWeeklySchedule(Long scheduleId) {
        DoctorWeeklySchedule schedule = weeklyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly schedule not found with id: " + scheduleId));

        Long doctorId = schedule.getDoctor().getDoctorId();
        CustomDayOfWeek dayOfWeek = schedule.getDayOfWeek();

        // Delete all time slots
        List<WeeklyTimeSlot> timeSlots = timeSlotRepository.findByWeeklySchedule(schedule);
        timeSlotRepository.deleteAll(timeSlots);

        weeklyScheduleRepository.delete(schedule);

        // Regenerate appointment slots for upcoming dates with this day of week
        regenerateAppointmentSlotsForDay(doctorId, dayOfWeek);
    }

    @Override
    @Transactional
    public void setDayAvailability(Long doctorId, CustomDayOfWeek dayOfWeek, boolean isAvailable) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        DoctorWeeklySchedule schedule = weeklyScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            schedule = new DoctorWeeklySchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(dayOfWeek);
        }

        schedule.setAvailable(isAvailable);
        weeklyScheduleRepository.save(schedule);

        // Regenerate appointment slots for upcoming dates with this day of week
        regenerateAppointmentSlotsForDay(doctorId, dayOfWeek);
    }
    
    @Override
    @Transactional
    public WeeklyScheduleDTO toggleDayAvailability(Long doctorId, CustomDayOfWeek dayOfWeek) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        DoctorWeeklySchedule schedule = weeklyScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            schedule = new DoctorWeeklySchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(dayOfWeek);
            schedule.setAvailable(true); // Default to available if creating new
        } else {
            // Toggle the current availability
            schedule.setAvailable(!schedule.isAvailable());
        }

        DoctorWeeklySchedule savedSchedule = weeklyScheduleRepository.save(schedule);

        // Regenerate appointment slots for upcoming dates with this day of week
        regenerateAppointmentSlotsForDay(doctorId, dayOfWeek);
        
        List<WeeklyTimeSlot> timeSlots = timeSlotRepository.findByWeeklySchedule(savedSchedule);
        return weeklyScheduleMapper.toDTO(savedSchedule, timeSlots);
    }

    private void regenerateAppointmentSlotsForDay(Long doctorId, CustomDayOfWeek dayOfWeek) {
        // Get the next 30 days
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        // For each date that matches the day of week, regenerate appointment slots
        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (convertToDayOfWeek(date) == dayOfWeek) {
                appointmentSlotService.generateAppointmentSlots(doctorId, date);
            }
        }
    }

    private CustomDayOfWeek convertToDayOfWeek(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return CustomDayOfWeek.MONDAY;
            case TUESDAY: return CustomDayOfWeek.TUESDAY;
            case WEDNESDAY: return CustomDayOfWeek.WEDNESDAY;
            case THURSDAY: return CustomDayOfWeek.THURSDAY;
            case FRIDAY: return CustomDayOfWeek.FRIDAY;
            case SATURDAY: return CustomDayOfWeek.SATURDAY;
            case SUNDAY: return CustomDayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Invalid day of week: " + date.getDayOfWeek());
        }
    }
}