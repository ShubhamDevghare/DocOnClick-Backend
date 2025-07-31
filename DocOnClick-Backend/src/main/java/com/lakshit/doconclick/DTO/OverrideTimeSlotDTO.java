package com.lakshit.doconclick.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverrideTimeSlotDTO {
    private Long id;
    private String startTime;
    private String endTime;
}