package com.lakshit.doconclick.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorStatsDTO {
    private Long totalPatientsCurrentMonth;  // Distinct patients for current month
    private Double currentMonthRevenue;    
    private Long todaysAppointments;
    private Double averageRating;
}
