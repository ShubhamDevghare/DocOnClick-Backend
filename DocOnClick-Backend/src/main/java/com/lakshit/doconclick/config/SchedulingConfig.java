// This class is used to automatically send reminder emails every day at 8:00 AM to 
// patients who have an appointment scheduled for tomorrow and that is confirmed.

package com.lakshit.doconclick.config;

import com.lakshit.doconclick.Repository.AppointmentRepository;
import com.lakshit.doconclick.Service.IEmailService;
import com.lakshit.doconclick.entity.Appointment;
import com.lakshit.doconclick.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {

    private final AppointmentRepository appointmentRepository;
    private final IEmailService emailService;

    // Run every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Find all confirmed appointments for tomorrow
        List<Appointment> tomorrowAppointments = appointmentRepository.findByAppointmentDateAndAppointmentStatus(
                tomorrow, AppointmentStatus.CONFIRMED);
        
    }
}
