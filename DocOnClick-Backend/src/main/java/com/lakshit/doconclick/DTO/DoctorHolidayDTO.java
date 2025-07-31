	package com.lakshit.doconclick.DTO;
	
	import lombok.AllArgsConstructor;
	import lombok.Builder;
	import lombok.Data;
	import lombok.NoArgsConstructor;
	
	import java.time.LocalDate;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public class DoctorHolidayDTO {
	    private Long id;
	    private Long doctorId;
	    private LocalDate holidayDate;
	    private String reason;
	}
