package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PaymentDTO;
import com.lakshit.doconclick.DTO.PaymentRequestDTO;
import com.lakshit.doconclick.DTO.RazorpayOrderDTO;
import com.lakshit.doconclick.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {
    RazorpayOrderDTO createRazorpayOrder(Long appointmentId);
    PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO);
    PaymentDTO getPaymentByAppointmentId(Long appointmentId);
    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);
    
    // Existing methods
    Page<PaymentDTO> getAllPayments(Pageable pageable);
    List<PaymentDTO> getAllPaymentsList();
    PaymentDTO getPaymentByReceiptNumber(String receiptNumber);
    List<PaymentDTO> searchPaymentsByReceiptNumber(String receiptNumber);
    PaymentDTO getPaymentByAppointmentIdDetailed(Long appointmentId);
    
    // New filtering methods
    Page<PaymentDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable);
    Page<PaymentDTO> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<PaymentDTO> getPaymentsByStatusAndDateRange(PaymentStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<PaymentDTO> getPaymentsByPatientName(String patientName);
    List<PaymentDTO> getPaymentsByDoctorName(String doctorName);
}
