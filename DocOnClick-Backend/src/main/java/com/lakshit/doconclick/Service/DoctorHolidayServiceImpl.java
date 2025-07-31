package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshit.doconclick.DTO.DoctorHolidayDTO;
import com.lakshit.doconclick.Repository.DoctorHolidayRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorHoliday;
import com.lakshit.doconclick.mapper.DoctorHolidayMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorHolidayServiceImpl implements IDoctorHolidayService {

    private final DoctorHolidayRepository holidayRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorHolidayMapper holidayMapper;
    private final IAppointmentSlotService appointmentSlotService;
    private final IEmailService emailService; // Added email service

    @Override
    @Transactional
    public DoctorHolidayDTO addHoliday(Long doctorId, DoctorHolidayDTO holidayDTO) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        // Check if holiday for this date already exists
        if (holidayRepository.existsByDoctorDoctorIdAndHolidayDate(doctorId, holidayDTO.getHolidayDate())) {
            throw new RuntimeException("Holiday for this date already exists");
        }

        DoctorHoliday holiday = holidayMapper.toEntity(holidayDTO, doctor);
        DoctorHoliday savedHoliday = holidayRepository.save(holiday);

        // Update doctor's holiday status if it's today
        if (holiday.getHolidayDate().equals(LocalDate.now())) {
            doctor.setHoliday(true);
            doctorRepository.save(doctor);
        }

        // Regenerate time slots for this date to reflect holiday
        appointmentSlotService.generateAppointmentSlots(doctorId, holidayDTO.getHolidayDate());

        // Send holiday notification email
        emailService.sendDoctorHolidayNotificationEmail(
            doctor, 
            holidayDTO.getHolidayDate().toString(), 
            holidayDTO.getReason()
        );

        return holidayMapper.toDTO(savedHoliday);
    }

    @Override
    @Transactional
    public DoctorHolidayDTO markTodayAsHoliday(Long doctorId, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        LocalDate today = LocalDate.now();

        // Check if holiday for today already exists
        if (holidayRepository.existsByDoctorDoctorIdAndHolidayDate(doctorId, today)) {
            throw new RuntimeException("Holiday for today already exists");
        }

        // Create holiday entity
        DoctorHoliday holiday = DoctorHoliday.builder()
                .doctor(doctor)
                .holidayDate(today)
                .reason(reason)
                .build();

        DoctorHoliday savedHoliday = holidayRepository.save(holiday);

        // Update doctor's holiday status
        doctor.setHoliday(true);
        doctorRepository.save(doctor);

        // Regenerate time slots for today to reflect holiday
        appointmentSlotService.generateAppointmentSlots(doctorId, today);

        // Send holiday notification email
        emailService.sendDoctorHolidayNotificationEmail(
            doctor, 
            today.toString(), 
            reason
        );

        return holidayMapper.toDTO(savedHoliday);
    }

    @Override
    @Transactional
    public void removeHoliday(Long holidayId) {
        DoctorHoliday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday not found with id: " + holidayId));

        Long doctorId = holiday.getDoctor().getDoctorId();
        LocalDate holidayDate = holiday.getHolidayDate();

        holidayRepository.delete(holiday);

        // Update doctor's holiday status if it's today
        if (holidayDate.equals(LocalDate.now())) {
            Doctor doctor = holiday.getDoctor();
            doctor.setHoliday(false);
            doctorRepository.save(doctor);
        }

        // Regenerate time slots for this date
        appointmentSlotService.generateAppointmentSlots(doctorId, holidayDate);
    }

    @Override
    public List<DoctorHolidayDTO> getDoctorHolidays(Long doctorId) {
        return holidayRepository.findByDoctorDoctorId(doctorId).stream()
                .map(holidayMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDoctorOnHoliday(Long doctorId, LocalDate date) {
        return holidayRepository.existsByDoctorDoctorIdAndHolidayDate(doctorId, date);
    }
}
