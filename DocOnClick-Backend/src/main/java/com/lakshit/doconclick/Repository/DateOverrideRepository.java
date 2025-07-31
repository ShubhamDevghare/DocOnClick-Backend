package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.DateOverride;
import com.lakshit.doconclick.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DateOverrideRepository extends JpaRepository<DateOverride, Long> {
    List<DateOverride> findByDoctor(Doctor doctor);
    List<DateOverride> findByDoctorDoctorId(Long doctorId);
    Optional<DateOverride> findByDoctorAndOverrideDate(Doctor doctor, LocalDate date);
    List<DateOverride> findByDoctorAndOverrideDateBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);
    boolean existsByDoctorAndOverrideDate(Doctor doctor, LocalDate date);
}