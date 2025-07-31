package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.entity.Admin;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.entity.User;
import com.lakshit.doconclick.entity.Appointment;

public interface IEmailService {
    
    // User registration/login emails
    void sendUserRegistrationEmail(User user);
    void sendUserLoginEmail(User user);
    void sendUserUpdateEmail(User user, String updatedBy);
    
    // Doctor registration/login emails
    void sendDoctorRegistrationEmail(Doctor doctor);
    void sendDoctorLoginEmail(Doctor doctor);
    void sendDoctorVerificationEmail(Doctor doctor, String status);
    void sendDoctorUpdateEmail(Doctor doctor, String updatedBy);
    void sendDoctorHolidayNotificationEmail(Doctor doctor, String holidayDate, String reason);
    
    // Patient registration emails
    void sendPatientRegistrationEmail(Patient patient);
    void sendPatientUpdateEmail(Patient patient, String updatedBy);
    
    // Admin registration/login emails
    void sendAdminRegistrationEmail(Admin admin);
    void sendAdminLoginEmail(Admin admin);
    void sendAdminActivationEmail(Admin admin);
    void sendAdminUpdateEmail(Admin admin, String updatedBy);
    
    // Appointment status emails
    void sendAppointmentCompletedEmail(Appointment appointment);
    void sendAppointmentCancelledByDoctorEmail(Appointment appointment);
    
    // Welcome emails
    void sendWelcomeEmail(String email, String fullName, String userType);
    
    // Account status emails
    void sendAccountStatusEmail(String email, String fullName, String status, String userType);
    
    // Generic update notification email
    void sendAccountUpdateNotificationEmail(String email, String fullName, String userType, String updatedBy);
}
