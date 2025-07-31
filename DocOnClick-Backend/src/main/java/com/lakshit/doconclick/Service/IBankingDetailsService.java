package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.BankingDetailsRequestDTO;
import com.lakshit.doconclick.DTO.BankingDetailsResponseDTO;
import com.lakshit.doconclick.DTO.BankingDetailsUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBankingDetailsService {
    
    BankingDetailsResponseDTO createBankingDetails(BankingDetailsRequestDTO requestDTO);
    
    BankingDetailsResponseDTO getBankingDetailsById(Long id);
    
    BankingDetailsResponseDTO getBankingDetailsByDoctorId(Long doctorId);
    
    Page<BankingDetailsResponseDTO> getAllBankingDetails(Pageable pageable);
    
    List<BankingDetailsResponseDTO> getAllBankingDetailsList();
    
    BankingDetailsResponseDTO updateBankingDetails(Long id, BankingDetailsUpdateDTO updateDTO);
    
    BankingDetailsResponseDTO updateBankingDetailsByDoctorId(Long doctorId, BankingDetailsUpdateDTO updateDTO);
    
    void deleteBankingDetails(Long id);
    
    void deleteBankingDetailsByDoctorId(Long doctorId);
    
    List<BankingDetailsResponseDTO> searchByBankName(String bankName);
    
    BankingDetailsResponseDTO findByUpiId(String upiId);
    
    boolean existsByDoctorId(Long doctorId);
    
    boolean existsByAccountNumber(String accountNumber);
}
