package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.DocumentsVerificationDTO;
import com.lakshit.doconclick.DTO.VerificationUpdateDTO;
import com.lakshit.doconclick.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentsVerificationService {
    
    DocumentsVerificationDTO createDocumentsVerification(Long doctorId, DocumentsVerificationDTO dto);
    
    DocumentsVerificationDTO uploadDocuments(
            Long doctorId,
            MultipartFile governmentIdProof,
            MultipartFile medicalRegistrationCertificate,
            MultipartFile educationalCertificate,
            MultipartFile experienceCertificate,
            MultipartFile specializationCertificate
    );
    
    DocumentsVerificationDTO getDocumentsVerificationById(Long id);
    
    DocumentsVerificationDTO getDocumentsVerificationByDoctorId(Long doctorId);
    
    Page<DocumentsVerificationDTO> getAllDocumentsVerifications(Pageable pageable);
    
    Page<DocumentsVerificationDTO> getDocumentsVerificationsByStatus(VerificationStatus status, Pageable pageable);
    
    Page<DocumentsVerificationDTO> searchDocumentsVerificationsByDoctorName(String doctorName, Pageable pageable);
    
    Page<DocumentsVerificationDTO> filterDocumentsVerificationsBySpeciality(String speciality, Pageable pageable);
    
    Page<DocumentsVerificationDTO> filterDocumentsVerificationsByStatusAndSpeciality(
            VerificationStatus status, String speciality, Pageable pageable);
    
    DocumentsVerificationDTO updateVerificationStatus(Long id, VerificationUpdateDTO updateDTO);
    
    DocumentsVerificationDTO updateVerificationStatusByDoctorId(Long doctorId, VerificationUpdateDTO updateDTO);
    
    void deleteDocumentsVerification(Long id);
}
