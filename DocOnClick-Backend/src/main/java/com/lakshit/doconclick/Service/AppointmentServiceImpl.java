package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshit.doconclick.DTO.AppointmentRequestDTO;
import com.lakshit.doconclick.DTO.AppointmentResponseDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.AppointmentSlotRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.PatientRepository;
import com.lakshit.doconclick.Repository.UserRepository;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.AppointmentSlot;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.entity.User;
import com.lakshit.doconclick.enums.AppointmentStatus;
import com.lakshit.doconclick.enums.PaymentStatus;
import com.lakshit.doconclick.mapper.AppointmentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final IAppointmentSlotService appointmentSlotService;
    private final IEmailService emailService;

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request) {
        // For now, we'll allow creating an appointment without payment
        // The actual booking will happen after payment is confirmed

        User user = (request.getUserId() != null) ? userRepository.findById(request.getUserId()).orElse(null) : null;
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // Check if doctor is on holiday
        if (doctor.isHoliday()) {
            throw new RuntimeException("Doctor is on holiday today. Please select another date.");
        }
        
        AppointmentSlot appointmentSlot = appointmentSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        
        // Check if the time slot is already booked
        if (appointmentSlot.isBooked()) {
            throw new RuntimeException("This time slot is already booked. Please select another slot.");
        }
        
        // Create appointment with UNPAID status
        Appointment appointment = AppointmentMapper.toEntity(request, user, doctor, patient, appointmentSlot);
        appointment.setPaymentStatus(PaymentStatus.UNPAID);
        appointment.setAppointmentDate(appointmentSlot.getDate());
        appointment.setAppointmentTime(appointmentSlot.getStartTime());
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Mark the time slot as booked
        appointmentSlot.setBooked(true);
        appointmentSlotRepository.save(appointmentSlot);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponseDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Only complete if appointment is confirmed
        if (appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed appointments can be marked as completed");
        }
        
        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send completion email to both patient and doctor
        emailService.sendAppointmentCompletedEmail(savedAppointment);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        
        // Release the time slot
        AppointmentSlot appointmentSlot = appointment.getAppointmentSlot();
        appointmentSlot.setBooked(false);
        appointmentSlotRepository.save(appointmentSlot);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send cancellation email (this method will determine if it's cancelled by doctor or patient)
        emailService.sendAppointmentCancelledByDoctorEmail(savedAppointment);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }

    // ... rest of the methods remain the same as in your original implementation ...
    
    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByUser(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserId(userId, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorDoctorId(doctorId, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    @Transactional
    public AppointmentResponseDTO confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Only confirm if payment is completed
        if (appointment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Payment must be completed before confirming the appointment");
        }
        
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }
    
    @Override
    @Transactional
    public AppointmentResponseDTO rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        
        // Release the time slot
        AppointmentSlot appointmentSlot = appointment.getAppointmentSlot();
        appointmentSlot.setBooked(false);
        appointmentSlotRepository.save(appointmentSlot);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getUpcomingAppointments(Long userId, Pageable pageable) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByUserUserIdAndAppointmentDateGreaterThanEqualAndAppointmentStatusNot(
                userId, today, AppointmentStatus.CANCELLED, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getPastAppointments(Long userId, Pageable pageable) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByUserUserIdAndAppointmentDateLessThan(
                userId, today, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getAllAppointments(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserId(userId, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getFilteredAppointments(LocalDate date, String status, Pageable pageable) {
        if (date != null && status != null) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            return appointmentRepository.findByAppointmentDateAndAppointmentStatus(date, appointmentStatus, pageable)
                    .map(AppointmentMapper::toResponseDTO);
        } else if (date != null) {
            return appointmentRepository.findByAppointmentDate(date, pageable)
                    .map(AppointmentMapper::toResponseDTO);
        } else if (status != null) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            return appointmentRepository.findByAppointmentStatus(appointmentStatus, pageable)
                    .map(AppointmentMapper::toResponseDTO);
        } else {
            return appointmentRepository.findAll(pageable)
                    .map(AppointmentMapper::toResponseDTO);
        }
    }
    
    @Override
    public Page<AppointmentResponseDTO> getRecentDoctorAppointments(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctorId, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getTodayDoctorAppointments(Long doctorId, Pageable pageable) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByDoctorDoctorIdAndAppointmentDate(doctorId, today, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    @Override
    public Page<AppointmentResponseDTO> getAppointmentHistory(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserIdAndAppointmentStatusIn(
                userId, 
                List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED),
                pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
    
    public Page<AppointmentResponseDTO> getAppointmentsByDoctorAndDateRange(
        Long doctorId, 
        LocalDate startDate, 
        LocalDate endDate, 
        Pageable pageable
    ) {
        return appointmentRepository.findByDoctorDoctorIdAndAppointmentDateBetween(
            doctorId, 
            startDate, 
            endDate, 
            pageable
        ).map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDoctorAndDateRange(
        Long doctorId, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        return appointmentRepository.findByDoctorDoctorIdAndAppointmentDateBetween(
            doctorId, 
            startDate, 
            endDate
        ).stream()
        .map(AppointmentMapper::toResponseDTO)
        .collect(Collectors.toList());
    }
    
    // For a specific doctor
    @Override
    public long countTodayAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.countTodayAppointmentsByDoctorId(doctorId);
    }

    // For all doctors (admin)
    @Override
    public long countAllTodayAppointments() {
        return appointmentRepository.countAllTodayAppointments();
    }

    @Override
    public long countDistinctPatientsByDoctorIdForCurrentMonth(Long doctorId) {
        return appointmentRepository.countDistinctPatientsByDoctorIdForCurrentMonth(doctorId);
    }
    
    @Override
    public Double getCurrentMonthRevenue(Long doctorId) {
        return appointmentRepository.calculateCurrentMonthRevenueByDoctorId(doctorId);
    }

    // NEW METHOD IMPLEMENTATION: Get appointment history between specific patient and doctor
    @Override
    public Page<AppointmentResponseDTO> getPatientAppointmentHistoryWithDoctor(Long patientId, Long doctorId, Pageable pageable) {
        return appointmentRepository.findByPatientPatientIdAndDoctorDoctorIdOrderByAppointmentDateDesc(
            patientId, doctorId, pageable)
            .map(AppointmentMapper::toResponseDTO);
    }

    // MISSING METHODS IMPLEMENTATION - Adding these now
    
    @Override
    public Page<AppointmentResponseDTO> getAllCompletedAppointments(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserIdAndAppointmentStatus(
                userId, AppointmentStatus.COMPLETED, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> getAllCancelledAppointments(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserIdAndAppointmentStatus(
                userId, AppointmentStatus.CANCELLED, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> getAllPendingAppointments(Long userId, Pageable pageable) {
        return appointmentRepository.findByUserUserIdAndAppointmentStatus(
                userId, AppointmentStatus.PENDING, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> searchAppointmentsByPatientNameForUser(Long userId, String patientName, Pageable pageable) {
        return appointmentRepository.findByUserUserIdAndPatientNameContaining(
                userId, patientName, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> searchAppointmentsByPatientNameForDoctor(Long doctorId, String patientName, Pageable pageable) {
        return appointmentRepository.findByDoctorDoctorIdAndPatientNameContaining(
                doctorId, patientName, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }

    @Override
    public Page<AppointmentResponseDTO> searchAppointmentsByPatientName(String patientName, Pageable pageable) {
        return appointmentRepository.findByPatientNameContaining(
                patientName, pageable)
                .map(AppointmentMapper::toResponseDTO);
    }
}
