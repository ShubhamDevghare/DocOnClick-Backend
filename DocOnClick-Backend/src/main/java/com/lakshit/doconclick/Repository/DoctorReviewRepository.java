package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.DoctorReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, Long> {
    Page<DoctorReview> findByDoctorDoctorId(Long doctorId, Pageable pageable);
    
    Optional<DoctorReview> findByAppointmentId(Long appointmentId);
    
    boolean existsByAppointmentId(Long appointmentId);
    
    @Query("SELECT AVG(r.rating) FROM DoctorReview r WHERE r.doctor.doctorId = :doctorId")
    Double getAverageRatingByDoctorId(Long doctorId);
}
