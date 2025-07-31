package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.PaymentDTO;
import com.lakshit.doconclick.DTO.PaymentRequestDTO;
import com.lakshit.doconclick.DTO.RazorpayOrderDTO;
import com.lakshit.doconclick.Service.IPaymentService;
import com.lakshit.doconclick.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/create-order/{appointmentId}")
    public ResponseEntity<RazorpayOrderDTO> createRazorpayOrder(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(appointmentId));
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        return ResponseEntity.ok(paymentService.processPayment(paymentRequestDTO));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PaymentDTO> getPaymentByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.getPaymentByAppointmentId(appointmentId));
    }

    // New endpoints for enhanced functionality
    
    /**
     * Get all payments with pagination
     */
    @GetMapping("/all")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }

    /**
     * Get all payments as a list (without pagination)
     */
    @GetMapping("/list")
    public ResponseEntity<List<PaymentDTO>> getAllPaymentsList() {
        return ResponseEntity.ok(paymentService.getAllPaymentsList());
    }

    /**
     * Search payment by receipt number (exact match)
     */
    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<PaymentDTO> getPaymentByReceiptNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByReceiptNumber(receiptNumber));
    }

    /**
     * Search payments by receipt number (partial match)
     */
    @GetMapping("/search/receipt")
    public ResponseEntity<List<PaymentDTO>> searchPaymentsByReceiptNumber(@RequestParam String receiptNumber) {
        return ResponseEntity.ok(paymentService.searchPaymentsByReceiptNumber(receiptNumber));
    }

    /**
     * Get payment by appointment ID with detailed information
     */
    @GetMapping("/appointment/{appointmentId}/detailed")
    public ResponseEntity<PaymentDTO> getPaymentByAppointmentIdDetailed(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.getPaymentByAppointmentIdDetailed(appointmentId));
    }

    /**
     * Alternative endpoint for searching by appointment ID
     */
    @GetMapping("/search/appointment/{appointmentId}")
    public ResponseEntity<PaymentDTO> searchPaymentByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(paymentService.getPaymentByAppointmentIdDetailed(appointmentId));
    }

    /**
     * Filter payments by status
     */
    @GetMapping("/filter/status/{status}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status, pageable));
    }

    /**
     * Filter payments by date range
     */
    @GetMapping("/filter/date-range")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(startDate, endDate, pageable));
    }

    /**
     * Filter payments by status and date range
     */
    @GetMapping("/filter/status-date")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByStatusAndDateRange(
            @RequestParam PaymentStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatusAndDateRange(status, startDate, endDate, pageable));
    }

    /**
     * Search payments by patient name
     */
    @GetMapping("/search/patient")
    public ResponseEntity<List<PaymentDTO>> searchPaymentsByPatientName(@RequestParam String patientName) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatientName(patientName));
    }

    /**
     * Search payments by doctor name
     */
    @GetMapping("/search/doctor")
    public ResponseEntity<List<PaymentDTO>> searchPaymentsByDoctorName(@RequestParam String doctorName) {
        return ResponseEntity.ok(paymentService.getPaymentsByDoctorName(doctorName));
    }
}
