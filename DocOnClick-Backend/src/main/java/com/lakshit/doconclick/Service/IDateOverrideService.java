	package com.lakshit.doconclick.Service;
	
	
	import com.lakshit.doconclick.DTO.DateOverrideDTO;
	
	import java.time.LocalDate;
	import java.util.List;
	
	public interface IDateOverrideService {
	    DateOverrideDTO createOrUpdateDateOverride(Long doctorId, DateOverrideDTO overrideDTO);
	    DateOverrideDTO getDateOverride(Long overrideId);
	    DateOverrideDTO getDateOverrideByDoctorAndDate(Long doctorId, LocalDate date);
	    List<DateOverrideDTO> getDateOverridesByDoctor(Long doctorId);
	    List<DateOverrideDTO> getDateOverridesInRange(Long doctorId, LocalDate startDate, LocalDate endDate);
	    void deleteDateOverride(Long overrideId);
	}