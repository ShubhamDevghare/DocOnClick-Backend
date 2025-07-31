package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PaymentDTO;
import com.lakshit.doconclick.DTO.PaymentRequestDTO;
import com.lakshit.doconclick.DTO.RazorpayOrderDTO;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.Payment;
import com.lakshit.doconclick.enums.AppointmentStatus;
import com.lakshit.doconclick.enums.PaymentStatus;
import com.lakshit.doconclick.mapper.PaymentMapper;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public RazorpayOrderDTO createRazorpayOrder(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        // Check if payment already exists for this appointment
        Optional<Payment> existingPayment = paymentRepository.findByAppointment(appointment);
        
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            
            // If payment is already completed, throw error
            if (payment.getStatus() == PaymentStatus.PAID) {
                throw new RuntimeException("Payment already completed for this appointment");
            }
            
            // If payment exists but not completed, return existing order details
            if (payment.getRazorpayOrderId() != null && !payment.getRazorpayOrderId().isEmpty()) {
                return RazorpayOrderDTO.builder()
                        .orderId(payment.getRazorpayOrderId())
                        .amount(payment.getAmount())
                        .currency("INR")
                        .receipt(payment.getReceiptNumber())
                        .build();
            }
        }

        Doctor doctor = appointment.getDoctor();
        BigDecimal amount = doctor.getFees();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Doctor fees must be greater than zero");
        }

        // Convert to paise (Razorpay uses smallest currency unit)
        int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();
        String receiptId = "rcpt_" + UUID.randomUUID().toString().replace("-", "");

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            // Update existing payment or create new one
            Payment payment;
            if (existingPayment.isPresent()) {
                payment = existingPayment.get();
                payment.setRazorpayOrderId(orderId);
                payment.setAmount(amount);
                payment.setStatus(PaymentStatus.UNPAID);
                payment.setReceiptNumber(receiptId);
            } else {
                payment = Payment.builder()
                        .appointment(appointment)
                        .razorpayOrderId(orderId)
                        .amount(amount)
                        .status(PaymentStatus.UNPAID)
                        .receiptNumber(receiptId)
                        .build();
            }

            paymentRepository.save(payment);

            return RazorpayOrderDTO.builder()
                    .orderId(orderId)
                    .amount(amount)
                    .currency("INR")
                    .receipt(receiptId)
                    .build();

        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentRepository.findByRazorpayOrderId(paymentRequestDTO.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + paymentRequestDTO.getRazorpayOrderId()));

        // Check if payment is already processed
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment already processed for this order");
        }

        // Verify payment signature
        boolean isSignatureValid = verifyPaymentSignature(
                paymentRequestDTO.getRazorpayOrderId(),
                paymentRequestDTO.getRazorpayPaymentId(),
                paymentRequestDTO.getRazorpaySignature()
        );

        if (!isSignatureValid) {
            // Mark payment as failed
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment signature verification failed");
        }

        // Update payment details
        payment.setRazorpayPaymentId(paymentRequestDTO.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.PAID);

        // Update appointment payment status
        Appointment appointment = payment.getAppointment();
        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
        return paymentMapper.toDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentDTO getPaymentByAppointmentId(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        Payment payment = paymentRepository.findByAppointment(appointment)
                .orElseThrow(() -> new RuntimeException("Payment not found for appointment: " + appointmentId));

        return paymentMapper.toDTO(payment);
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, razorpayKeySecret);
        } catch (RazorpayException e) {
            throw new RuntimeException("Error verifying payment signature: " + e.getMessage(), e);
        }
    }

    // New method implementations
    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDTO);
    }

    @Override
    public List<PaymentDTO> getAllPaymentsList() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentByReceiptNumber(String receiptNumber) {
        Payment payment = paymentRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found with receipt number: " + receiptNumber));
        return paymentMapper.toDTO(payment);
    }

    @Override
    public List<PaymentDTO> searchPaymentsByReceiptNumber(String receiptNumber) {
        return paymentRepository.findByReceiptNumberContaining(receiptNumber)
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentByAppointmentIdDetailed(Long appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for appointment id: " + appointmentId));
        return paymentMapper.toDTO(payment);
    }

    // New filtering method implementations
    @Override
    public Page<PaymentDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable)
                .map(paymentMapper::toDTO);
    }

    @Override
    public Page<PaymentDTO> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return paymentRepository.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable)
                .map(paymentMapper::toDTO);
    }

    @Override
    public Page<PaymentDTO> getPaymentsByStatusAndDateRange(PaymentStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return paymentRepository.findByStatusAndCreatedAtBetween(status, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable)
                .map(paymentMapper::toDTO);
    }

    @Override
    public List<PaymentDTO> getPaymentsByPatientName(String patientName) {
        return paymentRepository.findByPatientNameContaining(patientName)
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByDoctorName(String doctorName) {
        return paymentRepository.findByDoctorNameContaining(doctorName)
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
