package com.lakshit.doconclick.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshit.doconclick.DTO.BankingDetailsRequestDTO;
import com.lakshit.doconclick.DTO.BankingDetailsResponseDTO;
import com.lakshit.doconclick.DTO.BankingDetailsUpdateDTO;
import com.lakshit.doconclick.Repository.BankingDetailsRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.entity.BankingDetails;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.mapper.BankingDetailsMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankingDetailsServiceImpl implements IBankingDetailsService {

    private final BankingDetailsRepository bankingDetailsRepository;
    private final DoctorRepository doctorRepository;
    private final BankingDetailsMapper bankingDetailsMapper;

    @Override
    @Transactional
    public BankingDetailsResponseDTO createBankingDetails(BankingDetailsRequestDTO requestDTO) {
        // Check if doctor exists
        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + requestDTO.getDoctorId()));

        // Check if banking details already exist for this doctor
        if (bankingDetailsRepository.existsByDoctorDoctorId(requestDTO.getDoctorId())) {
            throw new RuntimeException("Banking details already exist for this doctor");
        }

        // Check if account number already exists
        if (bankingDetailsRepository.existsByAccountNumber(requestDTO.getAccountNumber())) {
            throw new RuntimeException("Account number already exists");
        }

        BankingDetails bankingDetails = bankingDetailsMapper.toEntity(requestDTO, doctor);
        BankingDetails savedBankingDetails = bankingDetailsRepository.save(bankingDetails);
        
        return bankingDetailsMapper.toResponseDTO(savedBankingDetails);
    }

    @Override
    public BankingDetailsResponseDTO getBankingDetailsById(Long id) {
        BankingDetails bankingDetails = bankingDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banking details not found with id: " + id));
        
        return bankingDetailsMapper.toResponseDTO(bankingDetails);
    }

    @Override
    public BankingDetailsResponseDTO getBankingDetailsByDoctorId(Long doctorId) {
        BankingDetails bankingDetails = bankingDetailsRepository.findByDoctorDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Banking details not found for doctor id: " + doctorId));
        
        return bankingDetailsMapper.toResponseDTO(bankingDetails);
    }

    @Override
    public Page<BankingDetailsResponseDTO> getAllBankingDetails(Pageable pageable) {
        return bankingDetailsRepository.findAll(pageable)
                .map(bankingDetailsMapper::toResponseDTO);
    }

    @Override
    public List<BankingDetailsResponseDTO> getAllBankingDetailsList() {
        return bankingDetailsRepository.findAll()
                .stream()
                .map(bankingDetailsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BankingDetailsResponseDTO updateBankingDetails(Long id, BankingDetailsUpdateDTO updateDTO) {
        BankingDetails existingBankingDetails = bankingDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banking details not found with id: " + id));

        // Check if account number is being changed and if it already exists
        if (!existingBankingDetails.getAccountNumber().equals(updateDTO.getAccountNumber()) &&
            bankingDetailsRepository.existsByAccountNumber(updateDTO.getAccountNumber())) {
            throw new RuntimeException("Account number already exists");
        }

        bankingDetailsMapper.updateEntityFromDTO(updateDTO, existingBankingDetails);
        BankingDetails updatedBankingDetails = bankingDetailsRepository.save(existingBankingDetails);
        
        return bankingDetailsMapper.toResponseDTO(updatedBankingDetails);
    }

    @Override
    @Transactional
    public BankingDetailsResponseDTO updateBankingDetailsByDoctorId(Long doctorId, BankingDetailsUpdateDTO updateDTO) {
        BankingDetails existingBankingDetails = bankingDetailsRepository.findByDoctorDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Banking details not found for doctor id: " + doctorId));

        // Check if account number is being changed and if it already exists
        if (!existingBankingDetails.getAccountNumber().equals(updateDTO.getAccountNumber()) &&
            bankingDetailsRepository.existsByAccountNumberAndIdNot(updateDTO.getAccountNumber(), existingBankingDetails.getId())) {
            throw new RuntimeException("Account number already exists");
        }

        bankingDetailsMapper.updateEntityFromDTO(updateDTO, existingBankingDetails);
        BankingDetails updatedBankingDetails = bankingDetailsRepository.save(existingBankingDetails);
        
        return bankingDetailsMapper.toResponseDTO(updatedBankingDetails);
    }

    @Override
    @Transactional
    public void deleteBankingDetails(Long id) {
        if (!bankingDetailsRepository.existsById(id)) {
            throw new RuntimeException("Banking details not found with id: " + id);
        }
        bankingDetailsRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBankingDetailsByDoctorId(Long doctorId) {
        if (!bankingDetailsRepository.existsByDoctorDoctorId(doctorId)) {
            throw new RuntimeException("Banking details not found for doctor id: " + doctorId);
        }
        bankingDetailsRepository.deleteByDoctorDoctorId(doctorId);
    }

    @Override
    public List<BankingDetailsResponseDTO> searchByBankName(String bankName) {
        return bankingDetailsRepository.findByBankNameContaining(bankName)
                .stream()
                .map(bankingDetailsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BankingDetailsResponseDTO findByUpiId(String upiId) {
        BankingDetails bankingDetails = bankingDetailsRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("Banking details not found with UPI ID: " + upiId));
        
        return bankingDetailsMapper.toResponseDTO(bankingDetails);
    }

    @Override
    public boolean existsByDoctorId(Long doctorId) {
        return bankingDetailsRepository.existsByDoctorDoctorId(doctorId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return bankingDetailsRepository.existsByAccountNumber(accountNumber);
    }
}
