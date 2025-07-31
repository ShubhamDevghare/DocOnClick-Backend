package com.lakshit.doconclick.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.enums.VerificationStatus;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
   
    Optional<Doctor> findByEmail(String email);
    
//    List<Doctor> findByVerificationStatus(VerificationStatus status);
    
    @Query("SELECT d FROM Doctor d WHERE d.documentsVerification.verificationStatus = :status")
    List<Doctor> findByVerificationStatus(@Param("status") VerificationStatus status);

    
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
    
    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);
    
    Page<Doctor> findByAddressContainingIgnoreCase(String location, Pageable pageable);
    
    Page<Doctor> findBySpecializationContainingIgnoreCaseAndAddressContainingIgnoreCase(
            String specialization, String location, Pageable pageable);
    
    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization")
    List<String> findAllSpecialities();
    
    @Query("SELECT d FROM Doctor d WHERE d.fees BETWEEN :minFees AND :maxFees")
    Page<Doctor> findByFeesBetween(Double minFees, Double maxFees, Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE d.experienceYears >= :minExperience")
    Page<Doctor> findByExperienceYearsGreaterThanEqual(Integer minExperience, Pageable pageable);

//	long countByVerificationStatus(VerificationStatus pending);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.documentsVerification.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") VerificationStatus status);

}
