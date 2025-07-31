package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.MedicalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {
    List<MedicalReport> findByPatientPatientId(Long patientId);
    List<MedicalReport> findByDoctorDoctorId(Long doctorId);
    List<MedicalReport> findByAppointmentId(Long appointmentId);
    Page<MedicalReport> findByPatientPatientId(Long patientId, Pageable pageable);
    
    @Query("SELECT mr FROM MedicalReport mr WHERE mr.patient.patientId = :patientId AND mr.fileType = :fileType")
    List<MedicalReport> findByPatientIdAndFileType(Long patientId, String fileType);
}
