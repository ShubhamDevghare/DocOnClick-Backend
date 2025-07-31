package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Payment;
import com.lakshit.doconclick.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointment(Appointment appointment);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    
    // New methods for enhanced functionality
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    List<Payment> findByReceiptNumberContaining(String receiptNumber);
    Optional<Payment> findByAppointmentId(Long appointmentId);
    
    // Additional methods for admin dashboard
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.appointment a WHERE a.appointmentDate = :date")
    BigDecimal sumAmountByDate(@Param("date") LocalDate date);
    
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    // Search and filter methods
    @Query("SELECT p FROM Payment p WHERE p.appointment.patient.fullName LIKE %:patientName%")
    List<Payment> findByPatientNameContaining(@Param("patientName") String patientName);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.doctor.fullName LIKE %:doctorName%")
    List<Payment> findByDoctorNameContaining(@Param("doctorName") String doctorName);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.doctor.doctorId = :doctorId")
    List<Payment> findByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.patient.patientId = :patientId")
    List<Payment> findByPatientId(@Param("patientId") Long patientId);

    // Date range filtering methods
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByStatusAndCreatedAtBetween(@Param("status") PaymentStatus status,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);

    // Additional utility methods
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :date")
    List<Payment> findPaymentsFromDate(@Param("date") LocalDateTime date);
}
