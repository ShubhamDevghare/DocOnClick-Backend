package com.lakshit.doconclick.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakshit.doconclick.entity.DoctorWeeklySchedule;
import com.lakshit.doconclick.entity.WeeklyTimeSlot;

@Repository
public interface WeeklyTimeSlotRepository extends JpaRepository<WeeklyTimeSlot, Long> {
    List<WeeklyTimeSlot> findByWeeklySchedule(DoctorWeeklySchedule weeklySchedule);
    List<WeeklyTimeSlot> findByWeeklyScheduleId(Long weeklyScheduleId);
    void deleteByWeeklyScheduleId(Long weeklyScheduleId);
}