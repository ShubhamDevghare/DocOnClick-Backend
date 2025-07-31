package com.lakshit.doconclick.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakshit.doconclick.DTO.PrescriptionDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.PrescriptionRepository;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.entity.Prescription;
import com.lakshit.doconclick.entity.PrescriptionMedicine;
import com.lakshit.doconclick.mapper.PrescriptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements IPrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Override
    @Transactional
    public PrescriptionDTO createPrescription(PrescriptionDTO prescriptionDTO) {
        Appointment appointment = appointmentRepository.findById(prescriptionDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + prescriptionDTO.getAppointmentId()));

        // Check if prescription already exists for this appointment
        prescriptionRepository.findByAppointment(appointment).ifPresent(existing -> {
            throw new RuntimeException("Prescription already exists for this appointment");
        });

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDTO, appointment);
        
        // Set prescription reference for medicines
        if (prescription.getMedicines() != null) {
            for (PrescriptionMedicine medicine : prescription.getMedicines()) {
                medicine.setPrescription(prescription);
            }
        }
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toDTO(savedPrescription);
    }

    @Override
    @Transactional
    public PrescriptionDTO updatePrescription(Long prescriptionId, PrescriptionDTO prescriptionDTO) {
        Prescription existingPrescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + prescriptionId));

        Appointment appointment = appointmentRepository.findById(prescriptionDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + prescriptionDTO.getAppointmentId()));

        // Update prescription fields
        existingPrescription.setDiagnosis(prescriptionDTO.getDiagnosis());
        existingPrescription.setNotes(prescriptionDTO.getNotes());
        
        // Clear existing medicines
        existingPrescription.getMedicines().clear();
        
        // Add new medicines
        if (prescriptionDTO.getMedicines() != null) {
            prescriptionDTO.getMedicines().forEach(medicineDTO -> {
                PrescriptionMedicine medicine = PrescriptionMedicine.builder()
                        .medicineName(medicineDTO.getMedicineName())
                        .dosage(medicineDTO.getDosage())
                        .frequency(medicineDTO.getFrequency())
                        .durationDays(medicineDTO.getDurationDays())
                        .instructions(medicineDTO.getInstructions())
                        .prescription(existingPrescription)
                        .build();
                existingPrescription.getMedicines().add(medicine);
            });
        }
        
        Prescription savedPrescription = prescriptionRepository.save(existingPrescription);
        return prescriptionMapper.toDTO(savedPrescription);
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + prescriptionId));
        return prescriptionMapper.toDTO(prescription);
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Prescription not found for appointment id: " + appointmentId));
        return prescriptionMapper.toDTO(prescription);
    }

    @Override
    @Transactional
    public void deletePrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + prescriptionId));
        prescriptionRepository.delete(prescription);
    }
}
