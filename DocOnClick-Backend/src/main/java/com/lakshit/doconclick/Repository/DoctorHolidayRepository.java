package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorHolidayRepository extends JpaRepository<DoctorHoliday, Long> {
    List<DoctorHoliday> findByDoctor(Doctor doctor);
    List<DoctorHoliday> findByDoctorDoctorId(Long doctorId);
    Optional<DoctorHoliday> findByDoctorAndHolidayDate(Doctor doctor, LocalDate date);
    boolean existsByDoctorDoctorIdAndHolidayDate(Long doctorId, LocalDate date);
}
