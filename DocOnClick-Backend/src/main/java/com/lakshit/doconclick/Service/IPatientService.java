package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lakshit.doconclick.DTO.PatientDetailDTO;
import com.lakshit.doconclick.DTO.PatientRequestDTO;
import com.lakshit.doconclick.DTO.PatientResponseDTO;

public interface IPatientService {
    PatientResponseDTO registerPatient(PatientRequestDTO patientDTO);
//    PatientResponseDTO findOrCreatePatient(PatientRequestDTO patientDTO);
    Optional<PatientResponseDTO> findExistingPatient(PatientRequestDTO patientDTO);
    PatientResponseDTO getPatientById(Long id);
    List<PatientResponseDTO> getAllPatients();
    void deletePatient(Long id);
    
    PatientDetailDTO getCompletePatientDetails(Long patientId);
    List<PatientResponseDTO> searchPatientsByName(String name);
    PatientResponseDTO searchPatientByPhone(String phone);
    Page<PatientResponseDTO> filterPatientsByAppointmentDate(LocalDate appointmentDate, Pageable pageable);
    List<PatientResponseDTO> searchPatientsByPartialPhone(String phonePartial);
}
