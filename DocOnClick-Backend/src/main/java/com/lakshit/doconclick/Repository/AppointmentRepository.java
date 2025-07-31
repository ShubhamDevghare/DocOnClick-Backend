package com.lakshit.doconclick.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByUserUserId(Long userId, Pageable pageable);
    Page<Appointment> findByDoctorDoctorId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatientPatientIdAndAppointmentDateGreaterThanEqual(Long patientId, LocalDate date, Pageable pageable);
    Page<Appointment> findByUserUserIdAndAppointmentDateGreaterThanEqualAndAppointmentStatusNot(
            Long userId, LocalDate date, AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByUserUserIdAndAppointmentDateLessThan(Long userId, LocalDate date, Pageable pageable);
    Page<Appointment> findByAppointmentDateAndAppointmentStatus(LocalDate date, AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByAppointmentDate(LocalDate date, Pageable pageable);
    Page<Appointment> findByAppointmentStatus(AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByDoctorDoctorIdOrderByCreatedAtDesc(Long doctorId, Pageable pageable);
    Page<Appointment> findByDoctorDoctorIdAndAppointmentDate(Long doctorId, LocalDate date, Pageable pageable);
    Page<Appointment> findByUserUserIdAndAppointmentStatusIn(Long userId, List<AppointmentStatus> statuses, Pageable pageable);
    List<Appointment> findByAppointmentDateAndAppointmentStatus(LocalDate date, AppointmentStatus status);
    
    // by appointment  doctor id and appointment date
    Page<Appointment> findByDoctorDoctorIdAndAppointmentDateBetween(
            Long doctorId, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable
        );
        
    List<Appointment> findByDoctorDoctorIdAndAppointmentDateBetween(
        Long doctorId, 
        LocalDate startDate, 
        LocalDate endDate
    );

    // Count today's appointments for a specific doctor
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE a.doctor.doctorId = :doctorId " +
           "AND DATE(a.appointmentDate) = CURRENT_DATE")
    long countTodayAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

    // Count all today's appointments (admin view)
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE DATE(a.appointmentDate) = CURRENT_DATE")
    long countAllTodayAppointments();

    // Number of patient of perticular doctor in current month
    @Query("SELECT COUNT(DISTINCT a.patient) FROM Appointment a " +
           "WHERE a.doctor.doctorId = :doctorId " +
           "AND YEAR(a.appointmentDate) = YEAR(CURRENT_DATE) " +
           "AND MONTH(a.appointmentDate) = MONTH(CURRENT_DATE)")
    long countDistinctPatientsByDoctorIdForCurrentMonth(@Param("doctorId") Long doctorId);

    // Revenue for a specific doctor for the current month
    @Query(value = "SELECT COALESCE(SUM(p.amount), 0) FROM payments p " +
           "JOIN appointments a ON p.appointment_id = a.id " +
           "WHERE a.doctor_id = :doctorId " +
           "AND p.status = 'COMPLETED' " +
           "AND EXTRACT(YEAR FROM p.payment_date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND EXTRACT(MONTH FROM p.payment_date) = EXTRACT(MONTH FROM CURRENT_DATE)", 
           nativeQuery = true)
    Double calculateCurrentMonthRevenueByDoctorId(@Param("doctorId") Long doctorId);
    
    // Additional methods for admin dashboard
    long countByAppointmentStatus(AppointmentStatus status);
    long countByAppointmentDate(LocalDate date);
    long countByDoctorDoctorId(Long doctorId);

    // NEW METHOD: Get appointment history between specific patient and doctor
    Page<Appointment> findByPatientPatientIdAndDoctorDoctorIdOrderByAppointmentDateDesc(
        Long patientId, Long doctorId, Pageable pageable);

    // MISSING METHODS - Adding these now
    
    // Get appointments by user and status
    Page<Appointment> findByUserUserIdAndAppointmentStatus(Long userId, AppointmentStatus status, Pageable pageable);
    
    // Search appointments by patient name for specific user
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.user.userId = :userId " +
           "AND LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    Page<Appointment> findByUserUserIdAndPatientNameContaining(
        @Param("userId") Long userId, 
        @Param("patientName") String patientName, 
        Pageable pageable);
    
    // Search appointments by patient name for specific doctor
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.doctor.doctorId = :doctorId " +
           "AND LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    Page<Appointment> findByDoctorDoctorIdAndPatientNameContaining(
        @Param("doctorId") Long doctorId, 
        @Param("patientName") String patientName, 
        Pageable pageable);
    
    // Search appointments by patient name (general)
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    Page<Appointment> findByPatientNameContaining(
        @Param("patientName") String patientName, 
        Pageable pageable);
}
