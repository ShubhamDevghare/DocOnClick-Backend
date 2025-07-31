package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.DoctorReviewDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.DoctorReviewRepository;
import com.lakshit.doconclick.Repository.PatientRepository;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorReview;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.enums.AppointmentStatus;
import com.lakshit.doconclick.mapper.DoctorReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorReviewServiceImpl implements IDoctorReviewService {

    private final DoctorReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorReviewMapper reviewMapper;

    @Override
    @Transactional
    public DoctorReviewDTO createReview(DoctorReviewDTO reviewDTO) {
        // Validate appointment is completed and belongs to the patient
        Appointment appointment = appointmentRepository.findById(reviewDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getAppointmentStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot review an appointment that is not completed");
        }

        if (!appointment.getPatient().getPatientId().equals(reviewDTO.getPatientId())) {
            throw new RuntimeException("This appointment does not belong to the specified patient");
        }

        // Check if review already exists for this appointment
        if (reviewRepository.existsByAppointmentId(reviewDTO.getAppointmentId())) {
            throw new RuntimeException("Review already exists for this appointment");
        }

        Doctor doctor = doctorRepository.findById(reviewDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(reviewDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        DoctorReview review = reviewMapper.toEntity(reviewDTO, doctor, patient, appointment);
        DoctorReview savedReview = reviewRepository.save(review);

        return reviewMapper.toDTO(savedReview);
    }

    @Override
    @Transactional
    public DoctorReviewDTO updateReview(Long reviewId, DoctorReviewDTO reviewDTO) {
        DoctorReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Only allow updating rating and comment
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        DoctorReview updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    public DoctorReviewDTO getReviewById(Long reviewId) {
        DoctorReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return reviewMapper.toDTO(review);
    }

    @Override
    public Page<DoctorReviewDTO> getDoctorReviews(Long doctorId, Pageable pageable) {
        return reviewRepository.findByDoctorDoctorId(doctorId, pageable)
                .map(reviewMapper::toDTO);
    }

    @Override
    public Double getDoctorAverageRating(Long doctorId) {
        return reviewRepository.getAverageRatingByDoctorId(doctorId);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public boolean hasPatientReviewedAppointment(Long appointmentId) {
        return reviewRepository.existsByAppointmentId(appointmentId);
    }
}
