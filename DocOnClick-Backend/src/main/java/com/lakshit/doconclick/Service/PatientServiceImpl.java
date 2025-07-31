package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lakshit.doconclick.DTO.AppointmentResponseDTO;
import com.lakshit.doconclick.DTO.MedicalReportDTO;
import com.lakshit.doconclick.DTO.PatientDetailDTO;
import com.lakshit.doconclick.DTO.PatientRequestDTO;
import com.lakshit.doconclick.DTO.PatientResponseDTO;
import com.lakshit.doconclick.DTO.PrescriptionDTO;
import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Repository.MedicalReportRepository;
import com.lakshit.doconclick.Repository.PatientRepository;
import com.lakshit.doconclick.Repository.PrescriptionRepository;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.mapper.AppointmentMapper;
import com.lakshit.doconclick.mapper.PatientMapper;
import com.lakshit.doconclick.mapper.PrescriptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements IPatientService {
    
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalReportRepository medicalReportRepository;
    private final IMedicalReportService medicalReportService;
    private final PrescriptionMapper prescriptionMapper;
    private final IEmailService emailService; // Added email service

    @Override
    public PatientResponseDTO registerPatient(PatientRequestDTO patientDTO) {
        // First, try to find existing patient
        Optional<PatientResponseDTO> existingPatient = findExistingPatient(patientDTO);
        
        if (existingPatient.isPresent()) {
            return existingPatient.get();
        }
        
        // If no existing patient found, create new one
        Patient patient = PatientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        
        // Send registration email
        emailService.sendPatientRegistrationEmail(savedPatient);
        
        return PatientMapper.toResponseDTO(savedPatient);
    }

    @Override
    public Optional<PatientResponseDTO> findExistingPatient(PatientRequestDTO patientDTO) {
        // Find patient by matching all key criteria
        Optional<Patient> existingPatient = patientRepository.findByFullNameAndPhoneAndEmailAddressAndDateOfBirthAndGender(
            patientDTO.getFullName(),
            patientDTO.getPhone(),
            patientDTO.getEmailAddress(),
            patientDTO.getDateOfBirth(),
            patientDTO.getGender()
        );
        
        return existingPatient.map(PatientMapper::toResponseDTO);
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return PatientMapper.toResponseDTO(patient);
    }

    @Override
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public PatientDetailDTO getCompletePatientDetails(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Get appointments
        List<AppointmentResponseDTO> appointments = appointmentRepository
                .findByPatientPatientIdAndAppointmentDateGreaterThanEqual(patientId, LocalDate.now().minusYears(1), null)
                .getContent().stream()
                .map(AppointmentMapper::toResponseDTO)
                .collect(Collectors.toList());

        // Get prescriptions
        List<PrescriptionDTO> prescriptions = prescriptionRepository.findAll().stream()
                .filter(p -> p.getAppointment().getPatient().getPatientId().equals(patientId))
                .map(prescriptionMapper::toDTO)
                .collect(Collectors.toList());

        // Get medical reports
        List<MedicalReportDTO> medicalReports = medicalReportService.getPatientReports(patientId);

        return PatientDetailDTO.builder()
                .patientId(patient.getPatientId())
                .fullName(patient.getFullName())
                .gender(patient.getGender().toString())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .emailAddress(patient.getEmailAddress())
                .address(patient.getAddress())
                .appointments(appointments)
                .prescriptions(prescriptions)
                .medicalReports(medicalReports)
                .build();
    }

    @Override
    public List<PatientResponseDTO> searchPatientsByName(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(PatientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponseDTO searchPatientByPhone(String phone) {
        Patient patient = patientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Patient not found with phone: " + phone));
        return PatientMapper.toResponseDTO(patient);
    }

    @Override
    public Page<PatientResponseDTO> filterPatientsByAppointmentDate(LocalDate appointmentDate, Pageable pageable) {
        return patientRepository.findPatientsWithAppointmentOnDate(appointmentDate, pageable)
                .map(PatientMapper::toResponseDTO);
    }

    @Override
    public List<PatientResponseDTO> searchPatientsByPartialPhone(String phonePartial) {
        return patientRepository.findByPhoneContaining(phonePartial)
                .stream()
                .map(PatientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
