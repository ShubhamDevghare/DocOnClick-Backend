package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lakshit.doconclick.DTO.DoctorPasswordChangeDTO;
import com.lakshit.doconclick.DTO.DoctorRequestDTO;
import com.lakshit.doconclick.DTO.DoctorResponseDTO;
import com.lakshit.doconclick.DTO.DoctorStatsDTO;
import com.lakshit.doconclick.DTO.DoctorUpdateDTO;

public interface IDoctorService {
    DoctorResponseDTO signUp(DoctorRequestDTO doctorRequestDTO);
    DoctorResponseDTO login(String email, String password);
    DoctorResponseDTO getDoctorById(Long id);
    Page<DoctorResponseDTO> getAllDoctors(Pageable pageable);
    DoctorResponseDTO updateDoctor(Long id, DoctorUpdateDTO doctorUpdateDTO);
    DoctorResponseDTO updateSlotDuration(Long id, Integer durationMinutes);
    void deleteDoctor(Long id);
    Page<DoctorResponseDTO> searchDoctorsBySpeciality(String speciality, Pageable pageable);
    Page<DoctorResponseDTO> filterDoctors(String speciality, Double minRating, String location, Pageable pageable);
    Page<DoctorResponseDTO> sortDoctors(String by, String order, Pageable pageable);
    Page<DoctorResponseDTO> combinedSearch(String speciality, Double minRating, String sort, String order, Pageable pageable);
    List<String> getAllSpecialities();
    DoctorResponseDTO markTodayAsHoliday(Long id);
    DoctorResponseDTO markDateAsHoliday(Long id, LocalDate date);
        
    DoctorStatsDTO getDoctorStats(Long doctorId);
    void changePassword(Long doctorId, DoctorPasswordChangeDTO passwordChangeDTO);
}
