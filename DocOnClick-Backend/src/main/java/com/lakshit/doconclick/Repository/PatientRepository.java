package com.lakshit.doconclick.Repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    boolean existsByPhone(String phone);
    Optional<Patient> findByPhone(String phone);
    
    // New method to find existing patient by all matching criteria
    Optional<Patient> findByFullNameAndPhoneAndEmailAddressAndDateOfBirthAndGender(
        String fullName, 
        String phone, 
        String emailAddress, 
        LocalDate dateOfBirth, 
        Gender gender
    );
    
    // Alternative method with more flexible matching (case-insensitive name)
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.fullName) = LOWER(:fullName) AND " +
           "p.phone = :phone AND " +
           "LOWER(p.emailAddress) = LOWER(:emailAddress) AND " +
           "p.dateOfBirth = :dateOfBirth AND " +
           "p.gender = :gender")
    Optional<Patient> findByMatchingCriteria(
        String fullName, 
        String phone, 
        String emailAddress, 
        LocalDate dateOfBirth, 
        Gender gender
    );
    
    // Search patients by name (case-insensitive)
    List<Patient> findByFullNameContainingIgnoreCase(String name);
    
    // Now this will work with the relationship in place
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.appointments a WHERE a.appointmentDate = :date")
    Page<Patient> findPatientsWithAppointmentOnDate(LocalDate date, Pageable pageable);
    
    // Find patients by email
    Optional<Patient> findByEmailAddress(String email);
    
    // Search patients by partial phone number
    List<Patient> findByPhoneContaining(String phonePartial);
}
