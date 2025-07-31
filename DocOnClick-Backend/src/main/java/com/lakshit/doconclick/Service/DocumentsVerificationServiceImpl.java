package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.DocumentsVerificationDTO;
import com.lakshit.doconclick.DTO.VerificationUpdateDTO;
import com.lakshit.doconclick.Repository.DocumentsVerificationRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DocumentsVerification;
import com.lakshit.doconclick.enums.VerificationStatus;
import com.lakshit.doconclick.mapper.DocumentsVerificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentsVerificationServiceImpl implements IDocumentsVerificationService {

    private final DocumentsVerificationRepository documentsVerificationRepository;
    private final DoctorRepository doctorRepository;
    private final DocumentsVerificationMapper documentsVerificationMapper;
    private final CloudinaryFileUploadService cloudinaryService;
    private final IEmailService emailService; // Added email service

    @Override
    @Transactional
    public DocumentsVerificationDTO createDocumentsVerification(Long doctorId, DocumentsVerificationDTO dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        // Check if documents verification already exists for this doctor
        Optional<DocumentsVerification> existingVerification = documentsVerificationRepository.findByDoctorDoctorId(doctorId);
        if (existingVerification.isPresent()) {
            throw new RuntimeException("Documents verification already exists for this doctor");
        }

        DocumentsVerification documentsVerification = documentsVerificationMapper.toEntity(dto, doctor);
        documentsVerification.setVerificationStatus(VerificationStatus.PENDING);
        
        DocumentsVerification savedVerification = documentsVerificationRepository.save(documentsVerification);
        return documentsVerificationMapper.toDTO(savedVerification);
    }

    @Override
    @Transactional
    public DocumentsVerificationDTO uploadDocuments(
            Long doctorId,
            MultipartFile governmentIdProof,
            MultipartFile medicalRegistrationCertificate,
            MultipartFile educationalCertificate,
            MultipartFile experienceCertificate,
            MultipartFile specializationCertificate) {
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        // Get existing verification or create new one
        DocumentsVerification documentsVerification = documentsVerificationRepository
                .findByDoctorDoctorId(doctorId)
                .orElse(new DocumentsVerification());
        
        documentsVerification.setDoctor(doctor);
        documentsVerification.setVerificationStatus(VerificationStatus.PENDING);

        // Upload documents to Cloudinary and set URLs
        if (governmentIdProof != null && !governmentIdProof.isEmpty()) {
            validatePdfFile(governmentIdProof);
            String fileUrl = cloudinaryService.uploadFile(governmentIdProof);
            documentsVerification.setGovernmentIdProof(fileUrl);
        }
        
        if (medicalRegistrationCertificate != null && !medicalRegistrationCertificate.isEmpty()) {
            validatePdfFile(medicalRegistrationCertificate);
            String fileUrl = cloudinaryService.uploadFile(medicalRegistrationCertificate);
            documentsVerification.setMedicalRegistrationCertificate(fileUrl);
        }
        
        if (educationalCertificate != null && !educationalCertificate.isEmpty()) {
            validatePdfFile(educationalCertificate);
            String fileUrl = cloudinaryService.uploadFile(educationalCertificate);
            documentsVerification.setEducationalCertificate(fileUrl);
        }
        
        if (experienceCertificate != null && !experienceCertificate.isEmpty()) {
            validatePdfFile(experienceCertificate);
            String fileUrl = cloudinaryService.uploadFile(experienceCertificate);
            documentsVerification.setExperienceCertificate(fileUrl);
        }
        
        if (specializationCertificate != null && !specializationCertificate.isEmpty()) {
            validatePdfFile(specializationCertificate);
            String fileUrl = cloudinaryService.uploadFile(specializationCertificate);
            documentsVerification.setSpecializationCertificate(fileUrl);
        }

        DocumentsVerification savedVerification = documentsVerificationRepository.save(documentsVerification);
        return documentsVerificationMapper.toDTO(savedVerification);
    }

    @Override
    @Transactional
    public DocumentsVerificationDTO updateVerificationStatus(Long id, VerificationUpdateDTO updateDTO) {
        DocumentsVerification documentsVerification = documentsVerificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documents verification not found with id: " + id));
        
        documentsVerification.setVerificationStatus(updateDTO.getVerificationStatus());
        
        // If doctor is verified or rejected, send email notification
        if (updateDTO.getVerificationStatus() == VerificationStatus.VERIFIED) {
            Doctor doctor = documentsVerification.getDoctor();
            doctorRepository.save(doctor);            
            // Send verification success email
            emailService.sendDoctorVerificationEmail(doctor, "VERIFIED");
        } else if (updateDTO.getVerificationStatus() == VerificationStatus.REJECTED) {
            Doctor doctor = documentsVerification.getDoctor();
            
            // Send verification rejection email
            emailService.sendDoctorVerificationEmail(doctor, "REJECTED");
        }
        
        DocumentsVerification updatedVerification = documentsVerificationRepository.save(documentsVerification);
        return documentsVerificationMapper.toDTO(updatedVerification);
    }

    @Override
    @Transactional
    public DocumentsVerificationDTO updateVerificationStatusByDoctorId(Long doctorId, VerificationUpdateDTO updateDTO) {
        DocumentsVerification documentsVerification = documentsVerificationRepository.findByDoctorDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Documents verification not found for doctor id: " + doctorId));
        
        documentsVerification.setVerificationStatus(updateDTO.getVerificationStatus());
        
        // If doctor is verified or rejected, send email notification
        if (updateDTO.getVerificationStatus() == VerificationStatus.VERIFIED) {
            Doctor doctor = documentsVerification.getDoctor();
            doctorRepository.save(doctor);
            
            // Send verification success email
            emailService.sendDoctorVerificationEmail(doctor, "VERIFIED");
        } else if (updateDTO.getVerificationStatus() == VerificationStatus.REJECTED) {
            Doctor doctor = documentsVerification.getDoctor();
            
            // Send verification rejection email
            emailService.sendDoctorVerificationEmail(doctor, "REJECTED");
        }
        
        DocumentsVerification updatedVerification = documentsVerificationRepository.save(documentsVerification);
        return documentsVerificationMapper.toDTO(updatedVerification);
    }
    
    @Override
    public DocumentsVerificationDTO getDocumentsVerificationById(Long id) {
        DocumentsVerification documentsVerification = documentsVerificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documents verification not found with id: " + id));
        
        return documentsVerificationMapper.toDTO(documentsVerification);
    }

    @Override
    public DocumentsVerificationDTO getDocumentsVerificationByDoctorId(Long doctorId) {
        DocumentsVerification documentsVerification = documentsVerificationRepository.findByDoctorDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Documents verification not found for doctor id: " + doctorId));
        
        return documentsVerificationMapper.toDTO(documentsVerification);
    }

    @Override
    public Page<DocumentsVerificationDTO> getAllDocumentsVerifications(Pageable pageable) {
        return documentsVerificationRepository.findAll(pageable)
                .map(documentsVerificationMapper::toDTO);
    }

    @Override
    public Page<DocumentsVerificationDTO> getDocumentsVerificationsByStatus(VerificationStatus status, Pageable pageable) {
        return documentsVerificationRepository.findByVerificationStatus(status, pageable)
                .map(documentsVerificationMapper::toDTO);
    }

    @Override
    public Page<DocumentsVerificationDTO> searchDocumentsVerificationsByDoctorName(String doctorName, Pageable pageable) {
        return documentsVerificationRepository.findByDoctorFullNameContainingIgnoreCase(doctorName, pageable)
                .map(documentsVerificationMapper::toDTO);
    }

    @Override
    public Page<DocumentsVerificationDTO> filterDocumentsVerificationsBySpeciality(String speciality, Pageable pageable) {
        return documentsVerificationRepository.findByDoctorSpecializationContainingIgnoreCase(speciality, pageable)
                .map(documentsVerificationMapper::toDTO);
    }

    @Override
    public Page<DocumentsVerificationDTO> filterDocumentsVerificationsByStatusAndSpeciality(
            VerificationStatus status, String speciality, Pageable pageable) {
        return documentsVerificationRepository.findByVerificationStatusAndDoctorSpecializationContainingIgnoreCase(
                status, speciality, pageable)
                .map(documentsVerificationMapper::toDTO);
    }

    @Override
    @Transactional
    public void deleteDocumentsVerification(Long id) {
        DocumentsVerification documentsVerification = documentsVerificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documents verification not found with id: " + id));
        
        // Delete documents from Cloudinary
        if (documentsVerification.getGovernmentIdProof() != null) {
            cloudinaryService.deleteFile(documentsVerification.getGovernmentIdProof());
        }
        
        if (documentsVerification.getMedicalRegistrationCertificate() != null) {
            cloudinaryService.deleteFile(documentsVerification.getMedicalRegistrationCertificate());
        }
        
        if (documentsVerification.getEducationalCertificate() != null) {
            cloudinaryService.deleteFile(documentsVerification.getEducationalCertificate());
        }
        
        if (documentsVerification.getExperienceCertificate() != null) {
            cloudinaryService.deleteFile(documentsVerification.getExperienceCertificate());
        }
        
        if (documentsVerification.getSpecializationCertificate() != null) {
            cloudinaryService.deleteFile(documentsVerification.getSpecializationCertificate());
        }
        
        documentsVerificationRepository.delete(documentsVerification);
    }
    
    private void validatePdfFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new RuntimeException("File size exceeds 5MB limit");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }
    }
}
