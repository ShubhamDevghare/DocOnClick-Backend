package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.AppointmentSlot;
import com.lakshit.doconclick.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByDoctorAndDateAndIsBookedFalse(Doctor doctor, LocalDate date);
    List<AppointmentSlot> findByDoctorDoctorIdAndDateAndIsBookedFalse(Long doctorId, LocalDate date);
    List<AppointmentSlot> findByDoctorDoctorIdAndDateBetweenAndIsBookedFalse(Long doctorId, LocalDate startDate, LocalDate endDate);
    Optional<AppointmentSlot> findByDoctorAndDateAndStartTimeAndEndTime(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime);
    boolean existsByDoctorAndDateAndStartTimeAndEndTimeAndIsBookedTrue(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime);
    void deleteByDoctorAndDateAndIsBookedFalse(Doctor doctor, LocalDate date);
}