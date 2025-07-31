package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.BankingDetailsRequestDTO;
import com.lakshit.doconclick.DTO.BankingDetailsResponseDTO;
import com.lakshit.doconclick.DTO.BankingDetailsUpdateDTO;
import com.lakshit.doconclick.Service.IBankingDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banking-details")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class BankingDetailsController {

    private final IBankingDetailsService bankingDetailsService;

    @PostMapping
    public ResponseEntity<BankingDetailsResponseDTO> createBankingDetails(
            @Valid @RequestBody BankingDetailsRequestDTO requestDTO) {
        BankingDetailsResponseDTO response = bankingDetailsService.createBankingDetails(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankingDetailsResponseDTO> getBankingDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(bankingDetailsService.getBankingDetailsById(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<BankingDetailsResponseDTO> getBankingDetailsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(bankingDetailsService.getBankingDetailsByDoctorId(doctorId));
    }

    @GetMapping
    public ResponseEntity<Page<BankingDetailsResponseDTO>> getAllBankingDetails(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bankingDetailsService.getAllBankingDetails(pageable));
    }

    @GetMapping("/list")
    public ResponseEntity<List<BankingDetailsResponseDTO>> getAllBankingDetailsList() {
        return ResponseEntity.ok(bankingDetailsService.getAllBankingDetailsList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankingDetailsResponseDTO> updateBankingDetails(
            @PathVariable Long id,
            @Valid @RequestBody BankingDetailsUpdateDTO updateDTO) {
        return ResponseEntity.ok(bankingDetailsService.updateBankingDetails(id, updateDTO));
    }

    @PutMapping("/doctor/{doctorId}")
    public ResponseEntity<BankingDetailsResponseDTO> updateBankingDetailsByDoctorId(
            @PathVariable Long doctorId,
            @Valid @RequestBody BankingDetailsUpdateDTO updateDTO) {
        return ResponseEntity.ok(bankingDetailsService.updateBankingDetailsByDoctorId(doctorId, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankingDetails(@PathVariable Long id) {
        bankingDetailsService.deleteBankingDetails(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/doctor/{doctorId}")
    public ResponseEntity<Void> deleteBankingDetailsByDoctorId(@PathVariable Long doctorId) {
        bankingDetailsService.deleteBankingDetailsByDoctorId(doctorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/bank")
    public ResponseEntity<List<BankingDetailsResponseDTO>> searchByBankName(@RequestParam String bankName) {
        return ResponseEntity.ok(bankingDetailsService.searchByBankName(bankName));
    }

    @GetMapping("/search/upi")
    public ResponseEntity<BankingDetailsResponseDTO> findByUpiId(@RequestParam String upiId) {
        return ResponseEntity.ok(bankingDetailsService.findByUpiId(upiId));
    }

    @GetMapping("/exists/doctor/{doctorId}")
    public ResponseEntity<Boolean> existsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(bankingDetailsService.existsByDoctorId(doctorId));
    }

    @GetMapping("/exists/account")
    public ResponseEntity<Boolean> existsByAccountNumber(@RequestParam String accountNumber) {
        return ResponseEntity.ok(bankingDetailsService.existsByAccountNumber(accountNumber));
    }
}
