package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.DoctorReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDoctorReviewService {
    DoctorReviewDTO createReview(DoctorReviewDTO reviewDTO);
    DoctorReviewDTO updateReview(Long reviewId, DoctorReviewDTO reviewDTO);
    DoctorReviewDTO getReviewById(Long reviewId);
    Page<DoctorReviewDTO> getDoctorReviews(Long doctorId, Pageable pageable);
    Double getDoctorAverageRating(Long doctorId);
    void deleteReview(Long reviewId);
    boolean hasPatientReviewedAppointment(Long appointmentId);
}
