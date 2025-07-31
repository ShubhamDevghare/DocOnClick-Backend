package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lakshit.doconclick.DTO.AppointmentRequestDTO;
import com.lakshit.doconclick.DTO.AppointmentResponseDTO;

public interface IAppointmentService {
    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request);
    Page<AppointmentResponseDTO> getAppointmentsByUser(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId, Pageable pageable);
    AppointmentResponseDTO confirmAppointment(Long appointmentId);
    AppointmentResponseDTO cancelAppointment(Long appointmentId);
    AppointmentResponseDTO completeAppointment(Long appointmentId);
    AppointmentResponseDTO rejectAppointment(Long appointmentId);
    Page<AppointmentResponseDTO> getUpcomingAppointments(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getPastAppointments(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getAllAppointments(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getFilteredAppointments(LocalDate date, String status, Pageable pageable);
    Page<AppointmentResponseDTO> getRecentDoctorAppointments(Long doctorId, Pageable pageable);
    Page<AppointmentResponseDTO> getTodayDoctorAppointments(Long doctorId, Pageable pageable);
    Page<AppointmentResponseDTO> getAppointmentHistory(Long userId, Pageable pageable);
    
    // get appointment by doctor id and date range
    Page<AppointmentResponseDTO> getAppointmentsByDoctorAndDateRange(
            Long doctorId, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable
        );
        
    List<AppointmentResponseDTO> getAppointmentsByDoctorAndDateRange(
        Long doctorId, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    long countTodayAppointmentsByDoctorId(Long doctorId);
    long countAllTodayAppointments();// for admin
    long countDistinctPatientsByDoctorIdForCurrentMonth(Long doctorId);
    Double getCurrentMonthRevenue(Long doctorId);

    // NEW METHOD: Get appointment history between specific patient and doctor
    Page<AppointmentResponseDTO> getPatientAppointmentHistoryWithDoctor(Long patientId, Long doctorId, Pageable pageable);

    // MISSING METHODS - Adding these now
    Page<AppointmentResponseDTO> getAllCompletedAppointments(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getAllCancelledAppointments(Long userId, Pageable pageable);
    Page<AppointmentResponseDTO> getAllPendingAppointments(Long userId, Pageable pageable);
    
    // Search appointments by patient name for specific user
    Page<AppointmentResponseDTO> searchAppointmentsByPatientNameForUser(Long userId, String patientName, Pageable pageable);
    
    // Search appointments by patient name for specific doctor
    Page<AppointmentResponseDTO> searchAppointmentsByPatientNameForDoctor(Long doctorId, String patientName, Pageable pageable);
    
    // Search appointments by patient name (general)
    Page<AppointmentResponseDTO> searchAppointmentsByPatientName(String patientName, Pageable pageable);
}
