	package com.lakshit.doconclick.Service;
	
	import com.lakshit.doconclick.DTO.DateOverrideDTO;
	import com.lakshit.doconclick.DTO.OverrideTimeSlotDTO;
	import com.lakshit.doconclick.entity.DateOverride;
	import com.lakshit.doconclick.entity.Doctor;
	import com.lakshit.doconclick.entity.OverrideTimeSlot;
	import com.lakshit.doconclick.exception.ResourceNotFoundException;
	import com.lakshit.doconclick.mapper.DateOverrideMapper;
	import com.lakshit.doconclick.Repository.DateOverrideRepository;
	import com.lakshit.doconclick.Repository.DoctorRepository;
	import com.lakshit.doconclick.Repository.OverrideTimeSlotRepository;
	import com.lakshit.doconclick.Service.IAppointmentSlotService;
	import com.lakshit.doconclick.Service.IDateOverrideService;
	import lombok.RequiredArgsConstructor;
	import org.springframework.stereotype.Service;
	import org.springframework.transaction.annotation.Transactional;
	
	import java.time.LocalDate;
	import java.time.LocalTime;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.stream.Collectors;
	
	@Service
	@RequiredArgsConstructor
	public class DateOverrideServiceImpl implements IDateOverrideService {
	
	    private final DateOverrideRepository dateOverrideRepository;
	    private final OverrideTimeSlotRepository overrideTimeSlotRepository;
	    private final DoctorRepository doctorRepository;
	    private final DateOverrideMapper dateOverrideMapper;
	    private final IAppointmentSlotService appointmentSlotService;
	
	    @Override
	    @Transactional
	    public DateOverrideDTO createOrUpdateDateOverride(Long doctorId, DateOverrideDTO overrideDTO) {
	        Doctor doctor = doctorRepository.findById(doctorId)
	                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	
	        DateOverride override;
	        boolean isNew = false;
	
	        // Check if override already exists for this date
	        if (overrideDTO.getId() != null) {
	            override = dateOverrideRepository.findById(overrideDTO.getId())
	                    .orElseThrow(() -> new ResourceNotFoundException("Date override not found with id: " + overrideDTO.getId()));
	        } else {
	            // Try to find by doctor and date
	            override = dateOverrideRepository.findByDoctorAndOverrideDate(doctor, overrideDTO.getOverrideDate())
	                    .orElse(null);
	
	            if (override == null) {
	                override = new DateOverride();
	                override.setDoctor(doctor);
	                override.setOverrideDate(overrideDTO.getOverrideDate());
	                isNew = true;
	            }
	        }
	
	        override.setAvailable(overrideDTO.isAvailable());
	        DateOverride savedOverride = dateOverrideRepository.save(override);
	
	        // Handle time slots
	        if (overrideDTO.isAvailable() && overrideDTO.getTimeSlots() != null && !overrideDTO.getTimeSlots().isEmpty()) {
	            // Delete existing time slots if any
	            if (!isNew) {
	                List<OverrideTimeSlot> existingSlots = overrideTimeSlotRepository.findByDateOverride(savedOverride);
	                overrideTimeSlotRepository.deleteAll(existingSlots);
	            }
	
	            // Create new time slots
	            List<OverrideTimeSlot> newTimeSlots = new ArrayList<>();
	            for (OverrideTimeSlotDTO slotDTO : overrideDTO.getTimeSlots()) {
	                OverrideTimeSlot timeSlot = new OverrideTimeSlot();
	                timeSlot.setStartTime(LocalTime.parse(slotDTO.getStartTime()));
	                timeSlot.setEndTime(LocalTime.parse(slotDTO.getEndTime()));
	                timeSlot.setDateOverride(savedOverride);
	                newTimeSlots.add(overrideTimeSlotRepository.save(timeSlot));
	            }
	        }
	
	        // Regenerate appointment slots for this date
	        appointmentSlotService.generateAppointmentSlots(doctorId, overrideDTO.getOverrideDate());
	
	        return getDateOverride(savedOverride.getId());
	    }
	
	    @Override
	    public DateOverrideDTO getDateOverride(Long overrideId) {
	        DateOverride override = dateOverrideRepository.findById(overrideId)
	                .orElseThrow(() -> new ResourceNotFoundException("Date override not found with id: " + overrideId));
	
	        List<OverrideTimeSlot> timeSlots = overrideTimeSlotRepository.findByDateOverride(override);
	        return dateOverrideMapper.toDTO(override, timeSlots);
	    }
	
	    @Override
	    public DateOverrideDTO getDateOverrideByDoctorAndDate(Long doctorId, LocalDate date) {
	        Doctor doctor = doctorRepository.findById(doctorId)
	                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	
	        DateOverride override = dateOverrideRepository.findByDoctorAndOverrideDate(doctor, date)
	                .orElseThrow(() -> new ResourceNotFoundException("Date override not found for doctor id: " + doctorId + " and date: " + date));
	
	        List<OverrideTimeSlot> timeSlots = overrideTimeSlotRepository.findByDateOverride(override);
	        return dateOverrideMapper.toDTO(override, timeSlots);
	    }
	
	    @Override
	    public List<DateOverrideDTO> getDateOverridesByDoctor(Long doctorId) {
	        Doctor doctor = doctorRepository.findById(doctorId)
	                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	
	        List<DateOverride> overrides = dateOverrideRepository.findByDoctor(doctor);
	        
	        return overrides.stream()
	                .map(override -> {
	                    List<OverrideTimeSlot> timeSlots = overrideTimeSlotRepository.findByDateOverride(override);
	                    return dateOverrideMapper.toDTO(override, timeSlots);
	                })
	                .collect(Collectors.toList());
	    }
	
	    @Override
	    public List<DateOverrideDTO> getDateOverridesInRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
	        Doctor doctor = doctorRepository.findById(doctorId)
	                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	
	        List<DateOverride> overrides = dateOverrideRepository.findByDoctorAndOverrideDateBetween(doctor, startDate, endDate);
	        
	        return overrides.stream()
	                .map(override -> {
	                    List<OverrideTimeSlot> timeSlots = overrideTimeSlotRepository.findByDateOverride(override);
	                    return dateOverrideMapper.toDTO(override, timeSlots);
	                })
	                .collect(Collectors.toList());
	    }
	
	    @Override
	    @Transactional
	    public void deleteDateOverride(Long overrideId) {
	        DateOverride override = dateOverrideRepository.findById(overrideId)
	                .orElseThrow(() -> new ResourceNotFoundException("Date override not found with id: " + overrideId));
	
	        Long doctorId = override.getDoctor().getDoctorId();
	        LocalDate date = override.getOverrideDate();
	
	        // Delete all time slots
	        List<OverrideTimeSlot> timeSlots = overrideTimeSlotRepository.findByDateOverride(override);
	        overrideTimeSlotRepository.deleteAll(timeSlots);
	
	        dateOverrideRepository.delete(override);
	
	        // Regenerate appointment slots for this date
	        appointmentSlotService.generateAppointmentSlots(doctorId, date);
	    }
	}