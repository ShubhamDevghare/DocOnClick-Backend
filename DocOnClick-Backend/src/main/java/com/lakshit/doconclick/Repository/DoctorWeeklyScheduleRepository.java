package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorWeeklySchedule;
import com.lakshit.doconclick.enums.CustomDayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorWeeklyScheduleRepository extends JpaRepository<DoctorWeeklySchedule, Long> {
    List<DoctorWeeklySchedule> findByDoctor(Doctor doctor);
    List<DoctorWeeklySchedule> findByDoctorDoctorId(Long doctorId);
    Optional<DoctorWeeklySchedule> findByDoctorAndDayOfWeek(Doctor doctor, CustomDayOfWeek dayOfWeek);
    boolean existsByDoctorAndDayOfWeek(Doctor doctor, CustomDayOfWeek dayOfWeek);
}