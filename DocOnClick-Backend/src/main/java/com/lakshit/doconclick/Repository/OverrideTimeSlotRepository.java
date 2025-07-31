package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.DateOverride;
import com.lakshit.doconclick.entity.OverrideTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverrideTimeSlotRepository extends JpaRepository<OverrideTimeSlot, Long> {
    List<OverrideTimeSlot> findByDateOverride(DateOverride dateOverride);
    List<OverrideTimeSlot> findByDateOverrideId(Long dateOverrideId);
    void deleteByDateOverrideId(Long dateOverrideId);
}