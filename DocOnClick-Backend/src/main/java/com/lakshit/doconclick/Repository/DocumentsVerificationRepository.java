package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.DocumentsVerification;
import com.lakshit.doconclick.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentsVerificationRepository extends JpaRepository<DocumentsVerification, Long> {
    Optional<DocumentsVerification> findByDoctorDoctorId(Long doctorId);
    Page<DocumentsVerification> findByVerificationStatus(VerificationStatus status, Pageable pageable);
    Page<DocumentsVerification> findByDoctorFullNameContainingIgnoreCase(String name, Pageable pageable);
    Page<DocumentsVerification> findByDoctorSpecializationContainingIgnoreCase(String specialization, Pageable pageable);
    Page<DocumentsVerification> findByVerificationStatusAndDoctorSpecializationContainingIgnoreCase(
    VerificationStatus status, String specialization, Pageable pageable);
}
