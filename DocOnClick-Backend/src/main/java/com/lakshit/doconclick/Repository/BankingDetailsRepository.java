package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.BankingDetails;
import com.lakshit.doconclick.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankingDetailsRepository extends JpaRepository<BankingDetails, Long> {
    
    Optional<BankingDetails> findByDoctor(Doctor doctor);
    
    Optional<BankingDetails> findByDoctorDoctorId(Long doctorId);
    
    boolean existsByDoctorDoctorId(Long doctorId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumberAndIdNot(String accountNumber, Long id);
    
    @Query("SELECT bd FROM BankingDetails bd WHERE bd.bankName LIKE %:bankName%")
    List<BankingDetails> findByBankNameContaining(@Param("bankName") String bankName);
    
    @Query("SELECT bd FROM BankingDetails bd WHERE bd.upiId = :upiId")
    Optional<BankingDetails> findByUpiId(@Param("upiId") String upiId);
    
    void deleteByDoctorDoctorId(Long doctorId);
}
