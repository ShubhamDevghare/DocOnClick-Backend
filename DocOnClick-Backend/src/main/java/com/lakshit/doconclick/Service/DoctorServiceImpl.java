package com.lakshit.doconclick.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.DTO.DoctorPasswordChangeDTO;
import com.lakshit.doconclick.DTO.DoctorRequestDTO;
import com.lakshit.doconclick.DTO.DoctorResponseDTO;
import com.lakshit.doconclick.DTO.DoctorStatsDTO;
import com.lakshit.doconclick.DTO.DoctorUpdateDTO;
import com.lakshit.doconclick.Repository.DoctorHolidayRepository;
import com.lakshit.doconclick.Repository.DoctorRepository;
import com.lakshit.doconclick.Repository.DoctorReviewRepository;
import com.lakshit.doconclick.Repository.DocumentsVerificationRepository;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.DoctorHoliday;
import com.lakshit.doconclick.entity.DocumentsVerification;
import com.lakshit.doconclick.enums.VerificationStatus;
import com.lakshit.doconclick.mapper.DoctorMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final IAppointmentSlotService appointmentSlot;
    private final DoctorReviewRepository reviewRepository;
    private final DoctorHolidayRepository holidayRepository;
    private final DocumentsVerificationRepository documentsVerificationRepository;
    private final IDocumentsVerificationService documentsVerificationService;
    private final IEmailService emailService; // Added email service
    
    @Autowired
    private CloudinaryFileUploadService cloudinaryFileUploadService;
    
    private final IAppointmentService appointmentService;
    private final IDoctorReviewService reviewService;

    @Override
    @Transactional
    public DoctorResponseDTO signUp(DoctorRequestDTO doctorRequestDTO) {
        String profileImageUrl = null;

        if (doctorRequestDTO.getProfileImage() != null && !doctorRequestDTO.getProfileImage().isEmpty()) {
            MultipartFile image = doctorRequestDTO.getProfileImage();

            // Validate size/type
            if (image.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("Profile image exceeds 2MB size limit");
            }

            String contentType = image.getContentType();
            if (!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
                throw new RuntimeException("Only PNG and JPEG formats are supported");
            }

            profileImageUrl = cloudinaryFileUploadService.uploadImage(image);
        }

        Doctor doctor = doctorMapper.toEntity(doctorRequestDTO, profileImageUrl);
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Create a documents verification entry for the doctor
        DocumentsVerification documentsVerification = DocumentsVerification.builder()
                .doctor(savedDoctor)
                .verificationStatus(VerificationStatus.PENDING)
                .build();
        
        documentsVerificationRepository.save(documentsVerification);
        
        // Send registration email
        emailService.sendDoctorRegistrationEmail(savedDoctor);
        
        return doctorMapper.toDTO(savedDoctor);
    }
    
    @Override
    public DoctorResponseDTO login(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!doctor.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        // Check if doctor is verified
        DocumentsVerification verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Verification status not found"));
        
        if (verification.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new RuntimeException("Your account is not verified yet. Please wait for admin approval.");
        }

        // Send login email
        emailService.sendDoctorLoginEmail(doctor);

        return doctorMapper.toDTO(doctor);
    }

    
    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        return doctorMapper.toDTO(doctor);
    }

    @Override
    public Page<DoctorResponseDTO> getAllDoctors(Pageable pageable) {
        // Get all doctors
        Page<Doctor> doctorsPage = doctorRepository.findAll(pageable);
        
        // Filter out doctors that are not verified
        List<DoctorResponseDTO> verifiedDoctors = doctorsPage.getContent().stream()
                .filter(doctor -> {
                    Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                    return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
                })
                .map(doctorMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(verifiedDoctors, pageable, doctorsPage.getTotalElements());
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateDTO doctorUpdateDTO) {
        Doctor existingDoctor = doctorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doctor with ID " + id + " not found"));

        String profileImageUrl = null;
        MultipartFile image = doctorUpdateDTO.getProfileImage();
        
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("Profile image exceeds 2MB limit");
            }

            String contentType = image.getContentType();
            if (!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
                throw new RuntimeException("Only PNG and JPEG formats are supported");
            }

            // Delete old image if exists
            if (existingDoctor.getProfileImage() != null) {
                cloudinaryFileUploadService.deleteImage(existingDoctor.getProfileImage());
            }

            // Upload new image
            profileImageUrl = cloudinaryFileUploadService.uploadImage(image);
        }

        doctorMapper.updateFromUpdateDTO(doctorUpdateDTO, existingDoctor, profileImageUrl);
        
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);

        return doctorMapper.toDTO(updatedDoctor);
    }

    @Override
    @Transactional
    public DoctorResponseDTO updateSlotDuration(Long id, Integer durationMinutes) {
        if (durationMinutes <= 0) {
            throw new RuntimeException("Slot duration must be greater than zero");
        }
        
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        
        doctor.setSlotDurationMinutes(durationMinutes);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        
        return doctorMapper.toDTO(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
                
        // Delete profile image from Cloudinary if it exists
        if (doctor.getProfileImage() != null && !doctor.getProfileImage().isBlank()) {
            cloudinaryFileUploadService.deleteImage(doctor.getProfileImage());
        }
        
        // Delete documents verification if exists
        documentsVerificationRepository.findByDoctorDoctorId(id).ifPresent(verification -> {
            documentsVerificationService.deleteDocumentsVerification(verification.getDocumentsVerificationId());
        });
        
        doctorRepository.deleteById(id);
    }
    
    @Override
    public Page<DoctorResponseDTO> searchDoctorsBySpeciality(String speciality, Pageable pageable) {
        if (speciality == null || speciality.isEmpty()) {
            return getAllDoctors(pageable);
        }
        
        Page<Doctor> doctorsPage = doctorRepository.findBySpecializationContainingIgnoreCase(speciality, pageable);
        
        // Filter out doctors that are not verified
        List<DoctorResponseDTO> verifiedDoctors = doctorsPage.getContent().stream()
                .filter(doctor -> {
                    Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                    return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
                })
                .map(doctorMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(verifiedDoctors, pageable, doctorsPage.getTotalElements());
    }
    
    @Override
    public Page<DoctorResponseDTO> filterDoctors(String speciality, Double minRating, String location, Pageable pageable) {
        // This is a simplified implementation. In a real-world scenario, you might want to use
        // a more sophisticated approach like Criteria API or QueryDSL for complex filtering
        
        if (speciality == null && minRating == null && (location == null || location.isEmpty())) {
            return getAllDoctors(pageable);
        }
        
        Page<Doctor> doctors;
        
        if (speciality != null && !speciality.isEmpty()) {
            if (location != null && !location.isEmpty()) {
                doctors = doctorRepository.findBySpecializationContainingIgnoreCaseAndAddressContainingIgnoreCase(
                        speciality, location, pageable);
            } else {
                doctors = doctorRepository.findBySpecializationContainingIgnoreCase(speciality, pageable);
            }
        } else if (location != null && !location.isEmpty()) {
            doctors = doctorRepository.findByAddressContainingIgnoreCase(location, pageable);
        } else {
            doctors = doctorRepository.findAll(pageable);
        }
        
        // Filter out doctors that are not verified
        List<DoctorResponseDTO> filteredList = doctors
            .stream()
            .filter(doctor -> {
                Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
            })
            .filter(doctor -> {
                if (minRating != null) {
                    Double avgRating = reviewRepository.getAverageRatingByDoctorId(doctor.getDoctorId());
                    return avgRating != null && avgRating >= minRating;
                }
                return true;
            })
            .map(doctorMapper::toDTO)
            .toList();

        return new PageImpl<>(filteredList, doctors.getPageable(), filteredList.size());
    }
    
    @Override
    public Page<DoctorResponseDTO> sortDoctors(String by, String order, Pageable pageable) {
        Sort sort;
        
        if ("rating".equalsIgnoreCase(by)) {
            // Sorting by rating requires custom implementation since it's calculated
            // First, get all doctors
            List<Doctor> allDoctors = doctorRepository.findAll();
            
            // Filter out doctors that are not verified
            allDoctors = allDoctors.stream()
                    .filter(doctor -> {
                        Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                        return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
                    })
                    .collect(Collectors.toList());
            
            // Calculate average rating for each doctor
            List<Object[]> doctorsWithRatings = allDoctors.stream()
                    .map(doctor -> {
                        Double avgRating = reviewRepository.getAverageRatingByDoctorId(doctor.getDoctorId());
                        return new Object[]{doctor, avgRating != null ? avgRating : 0.0};
                    })
                    .collect(Collectors.toList());
            
            // Sort by rating
            if ("desc".equalsIgnoreCase(order)) {
                doctorsWithRatings.sort((a, b) -> Double.compare((Double) b[1], (Double) a[1]));
            } else {
                doctorsWithRatings.sort((a, b) -> Double.compare((Double) a[1], (Double) b[1]));
            }
            
            // Extract sorted doctors and convert to DTOs
            List<DoctorResponseDTO> sortedDoctorDTOs = doctorsWithRatings.stream()
                    .map(obj -> doctorMapper.toDTO((Doctor) obj[0]))
                    .collect(Collectors.toList());
            
            // Create a Page from the sorted list
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), sortedDoctorDTOs.size());
            
            return new org.springframework.data.domain.PageImpl<>(
                    sortedDoctorDTOs.subList(start, end),
                    pageable,
                    sortedDoctorDTOs.size());
        } else if ("experience".equalsIgnoreCase(by)) {
            // Sort by experience years
            sort = "desc".equalsIgnoreCase(order) 
                    ? Sort.by(Sort.Direction.DESC, "experienceYears")
                    : Sort.by(Sort.Direction.ASC, "experienceYears");
                    
            Page<Doctor> doctors = doctorRepository.findAll(PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort));
            
            // Filter out doctors that are not verified
            List<DoctorResponseDTO> verifiedDoctors = doctors.getContent().stream()
                    .filter(doctor -> {
                        Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                        return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
                    })
                    .map(doctorMapper::toDTO)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(verifiedDoctors, pageable, doctors.getTotalElements());
        } else {
            // Default sort by name
            sort = Sort.by(Sort.Direction.ASC, "fullName");
            Page<Doctor> doctors = doctorRepository.findAll(PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort));
            
            // Filter out doctors that are not verified
            List<DoctorResponseDTO> verifiedDoctors = doctors.getContent().stream()
                    .filter(doctor -> {
                        Optional<DocumentsVerification> verification = documentsVerificationRepository.findByDoctorDoctorId(doctor.getDoctorId());
                        return verification.isPresent() && verification.get().getVerificationStatus() == VerificationStatus.VERIFIED;
                    })
                    .map(doctorMapper::toDTO)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(verifiedDoctors, pageable, doctors.getTotalElements());
        }
    }
    
    @Override
    public Page<DoctorResponseDTO> combinedSearch(String speciality, Double minRating, String sort, String order, Pageable pageable) {
        // First filter by speciality and minRating
        Page<DoctorResponseDTO> filteredDoctors = filterDoctors(speciality, minRating, null, pageable);
        
        // Then sort if needed
        if (sort != null && !sort.isEmpty()) {
            return sortDoctors(sort, order != null ? order : "asc", pageable);
        }
        
        return filteredDoctors;
    }
    
    @Override
    public List<String> getAllSpecialities() {
        return doctorRepository.findAllSpecialities();
    }
    
    @Override
    @Transactional
    public DoctorResponseDTO markTodayAsHoliday(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        
        LocalDate today = LocalDate.now();
        
        // Check if holiday already exists for today
        if (holidayRepository.existsByDoctorDoctorIdAndHolidayDate(id, today)) {
            throw new RuntimeException("Holiday already marked for today");
        }
        
        // Create holiday record
        DoctorHoliday holiday = DoctorHoliday.builder()
                .doctor(doctor)
                .holidayDate(today)
                .reason("Doctor unavailable")
                .build();
        
        holidayRepository.save(holiday);
        
        // Update doctor's holiday status
        doctor.setHoliday(true);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        
        // Regenerate time slots for today
        appointmentSlot.generateAppointmentSlots(id, today);
        
        return doctorMapper.toDTO(updatedDoctor);
    }
    
    @Override
    @Transactional
    public DoctorResponseDTO markDateAsHoliday(Long id, LocalDate date) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        
        // Check if holiday already exists for this date
        if (holidayRepository.existsByDoctorDoctorIdAndHolidayDate(id, date)) {
            throw new RuntimeException("Holiday already marked for this date");
        }
        
        // Create holiday record
        DoctorHoliday holiday = DoctorHoliday.builder()
                .doctor(doctor)
                .holidayDate(date)
                .reason("Doctor unavailable")
                .build();
        
        holidayRepository.save(holiday);
        
        // Update doctor's holiday status if it's today
        if (date.equals(LocalDate.now())) {
            doctor.setHoliday(true);
            doctor = doctorRepository.save(doctor);
        }
        
        // Regenerate time slots for this date
        appointmentSlot.generateAppointmentSlots(id, date);
        
        return doctorMapper.toDTO(doctor);
    }
    
    @Override
    public DoctorStatsDTO getDoctorStats(Long doctorId) {
        return DoctorStatsDTO.builder()
                .totalPatientsCurrentMonth(
                    appointmentService.countDistinctPatientsByDoctorIdForCurrentMonth(doctorId)
                )
                .currentMonthRevenue(
                    appointmentService.getCurrentMonthRevenue(doctorId)
                )
                .todaysAppointments(
                    appointmentService.countTodayAppointmentsByDoctorId(doctorId)
                )
                .averageRating(
                	    Optional.ofNullable(reviewService.getDoctorAverageRating(doctorId))
                	        .orElse(0.0)
                	)
                .build();
    }
    @Override
    @Transactional
    public void changePassword(Long doctorId, DoctorPasswordChangeDTO passwordChangeDTO) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Verify current password
        if (!doctor.getPassword().equals(passwordChangeDTO.getCurrentPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update password
        doctor.setPassword(passwordChangeDTO.getNewPassword());
        doctorRepository.save(doctor);
    }
}
