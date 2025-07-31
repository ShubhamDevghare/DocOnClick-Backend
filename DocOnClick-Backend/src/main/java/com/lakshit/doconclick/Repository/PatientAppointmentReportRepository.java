package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.PatientAppointmentReport;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientAppointmentReportRepository extends JpaRepository<PatientAppointmentReport, Long> {
    
    // Find reports by appointment
    List<PatientAppointmentReport> findByAppointment(Appointment appointment);
    
    // Find reports by appointment ID
    List<PatientAppointmentReport> findByAppointmentId(Long appointmentId);
    
    // Find reports that a doctor can view (confirmed or completed appointments with that doctor)
    @Query("SELECT r FROM PatientAppointmentReport r " +
           "WHERE r.appointment.doctor.doctorId = :doctorId " +
           "AND r.appointment.appointmentStatus IN ('CONFIRMED', 'COMPLETED')")
    List<PatientAppointmentReport> findViewableReportsByDoctor(@Param("doctorId") Long doctorId);
    
    // Find reports for a specific patient's appointments
    @Query("SELECT r FROM PatientAppointmentReport r " +
           "WHERE r.appointment.patient.patientId = :patientId")
    List<PatientAppointmentReport> findByPatientId(@Param("patientId") Long patientId);
    
    // Check if doctor can view specific report
    @Query("SELECT COUNT(r) > 0 FROM PatientAppointmentReport r " +
           "WHERE r.id = :reportId " +
           "AND r.appointment.doctor.doctorId = :doctorId " +
           "AND r.appointment.appointmentStatus IN ('CONFIRMED', 'COMPLETED')")
    boolean canDoctorViewReport(@Param("reportId") Long reportId, @Param("doctorId") Long doctorId);
}
