package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointment(Appointment appointment);
    Optional<Prescription> findByAppointmentId(Long appointmentId);
}