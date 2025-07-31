package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.PrescriptionDTO;

public interface IPrescriptionService {
    PrescriptionDTO createPrescription(PrescriptionDTO prescriptionDTO);
    PrescriptionDTO updatePrescription(Long prescriptionId, PrescriptionDTO prescriptionDTO);
    PrescriptionDTO getPrescriptionById(Long prescriptionId);
    PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId);
    void deletePrescription(Long prescriptionId);
}