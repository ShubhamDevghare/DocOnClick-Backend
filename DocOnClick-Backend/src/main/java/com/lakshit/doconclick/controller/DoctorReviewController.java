package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.DoctorReviewDTO;
import com.lakshit.doconclick.Service.IDoctorReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class DoctorReviewController {

    private final IDoctorReviewService reviewService;

    @PostMapping
    public ResponseEntity<DoctorReviewDTO> createReview(@Valid @RequestBody DoctorReviewDTO reviewDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(reviewDTO));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<DoctorReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody DoctorReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewDTO));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<DoctorReviewDTO> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<DoctorReviewDTO>> getDoctorReviews(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getDoctorReviews(doctorId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/rating")
    public ResponseEntity<Double> getDoctorAverageRating(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorAverageRating(doctorId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{appointmentId}")
    public ResponseEntity<Boolean> hasPatientReviewedAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(reviewService.hasPatientReviewedAppointment(appointmentId));
    }
}
