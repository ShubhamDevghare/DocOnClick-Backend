package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.entity.Admin;
import com.lakshit.doconclick.entity.Doctor;
import com.lakshit.doconclick.entity.Patient;
import com.lakshit.doconclick.entity.User;
import com.lakshit.doconclick.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;
    
    @Async("taskExecutor")
    @Override
    public void sendUserRegistrationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to DocOnClick - Registration Successful");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
                "Welcome to DocOnClick! Your account has been successfully created.\n\n" +
                "Account Details:\n" +
                "Name: " + user.getFullName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhone() + "\n" +
                "Role: " + user.getRole() + "\n\n" +
                "You can now log in and start booking appointments with our verified doctors.\n\n" +
                "Thank you for choosing DocOnClick!\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendUserLoginEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("DocOnClick - Login Notification");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
                "You have successfully logged into your DocOnClick account.\n\n" +
                "Login Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If this wasn't you, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendUserUpdateEmail(User user, String updatedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("DocOnClick - Account Updated");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
                "Your DocOnClick account has been updated by " + updatedBy + ".\n\n" +
                "Updated Account Details:\n" +
                "Name: " + user.getFullName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhone() + "\n" +
                "Address: " + user.getAddress() + "\n" +
                "Role: " + user.getRole() + "\n\n" +
                "Update Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If you have any questions about these changes, please contact our support team.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendDoctorRegistrationEmail(Doctor doctor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctor.getEmail());
        message.setSubject("Welcome to DocOnClick - Doctor Registration Successful");
        message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                "Welcome to DocOnClick! Your doctor account has been successfully created.\n\n" +
                "Account Details:\n" +
                "Name: Dr. " + doctor.getFullName() + "\n" +
                "Email: " + doctor.getEmail() + "\n" +
                "Phone: " + doctor.getPhone() + "\n" +
                "Specialization: " + doctor.getSpecialization() + "\n" +
                "Experience: " + doctor.getExperienceYears() + " years\n" +
                "Medical License: " + doctor.getMedicalLicenseNumber() + "\n\n" +
                "Your account is currently under review. Our admin team will verify your documents and activate your account within 24-48 hours.\n\n" +
                "Once verified, you'll be able to:\n" +
                "- Set your availability schedule\n" +
                "- Manage appointments\n" +
                "- View patient reports\n" +
                "- Update your profile\n\n" +
                "Thank you for joining DocOnClick!\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendDoctorLoginEmail(Doctor doctor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctor.getEmail());
        message.setSubject("DocOnClick - Doctor Login Notification");
        message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                "You have successfully logged into your DocOnClick doctor portal.\n\n" +
                "Login Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If this wasn't you, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendDoctorVerificationEmail(Doctor doctor, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctor.getEmail());
        
        if ("VERIFIED".equals(status)) {
            message.setSubject("DocOnClick - Account Verified Successfully");
            message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                    "Congratulations! Your DocOnClick account has been successfully verified by our admin team.\n\n" +
                    "You can now:\n" +
                    "- Log in to your doctor portal\n" +
                    "- Set your availability and consultation fees\n" +
                    "- Start accepting patient appointments\n" +
                    "- Manage your schedule\n\n" +
                    "Verification completed on: " + java.time.LocalDateTime.now() + "\n\n" +
                    "Welcome to the DocOnClick family!\n\n" +
                    "Best regards,\n" +
                    "DocOnClick Team");
        } else if ("REJECTED".equals(status)) {
            message.setSubject("DocOnClick - Account Verification Update");
            message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                    "We regret to inform you that your DocOnClick account verification has been rejected by our admin team.\n\n" +
                    "This may be due to:\n" +
                    "- Incomplete or unclear documentation\n" +
                    "- Invalid medical license information\n" +
                    "- Missing required certificates\n\n" +
                    "Please contact our support team for more information about the verification process and resubmission guidelines.\n\n" +
                    "Support Email: support@doconclick.com\n" +
                    "Support Phone: +1-800-DOC-CLICK\n\n" +
                    "Best regards,\n" +
                    "DocOnClick Team");
        }
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendDoctorUpdateEmail(Doctor doctor, String updatedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctor.getEmail());
        message.setSubject("DocOnClick - Doctor Profile Updated");
        message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                "Your DocOnClick doctor profile has been updated by " + updatedBy + ".\n\n" +
                "Updated Profile Details:\n" +
                "Name: Dr. " + doctor.getFullName() + "\n" +
                "Email: " + doctor.getEmail() + "\n" +
                "Phone: " + doctor.getPhone() + "\n" +
                "Specialization: " + doctor.getSpecialization() + "\n" +
                "Experience: " + doctor.getExperienceYears() + " years\n" +
                "Consultation Fees: ₹" + (doctor.getFees() != null ? doctor.getFees() : "Not set") + "\n" +
                "Slot Duration: " + doctor.getSlotDurationMinutes() + " minutes\n\n" +
                "Update Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If you have any questions about these changes, please contact our support team.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendDoctorHolidayNotificationEmail(Doctor doctor, String holidayDate, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctor.getEmail());
        message.setSubject("DocOnClick - Holiday Status Updated");
        message.setText("Dear Dr. " + doctor.getFullName() + ",\n\n" +
                "Your holiday status has been updated in the DocOnClick system.\n\n" +
                "Holiday Details:\n" +
                "Date: " + holidayDate + "\n" +
                "Reason: " + (reason != null ? reason : "Doctor unavailable") + "\n" +
                "Status: Holiday marked\n\n" +
                "Important Notes:\n" +
                "- All appointment slots for this date have been automatically removed\n" +
                "- Patients will not be able to book appointments for this date\n" +
                "- Existing appointments (if any) may need to be rescheduled\n\n" +
                "You can manage your holiday schedule through your doctor portal.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendPatientRegistrationEmail(Patient patient) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(patient.getEmailAddress());
        message.setSubject("Welcome to DocOnClick - Patient Profile Created");
        message.setText("Dear " + patient.getFullName() + ",\n\n" +
                "Your patient profile has been successfully created in DocOnClick.\n\n" +
                "Patient Details:\n" +
                "Name: " + patient.getFullName() + "\n" +
                "Email: " + patient.getEmailAddress() + "\n" +
                "Phone: " + patient.getPhone() + "\n" +
                "Date of Birth: " + patient.getDateOfBirth() + "\n" +
                "Gender: " + patient.getGender() + "\n\n" +
                "Your profile is now ready for booking appointments with our verified doctors.\n\n" +
                "Thank you for choosing DocOnClick for your healthcare needs!\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendPatientUpdateEmail(Patient patient, String updatedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(patient.getEmailAddress());
        message.setSubject("DocOnClick - Patient Profile Updated");
        message.setText("Dear " + patient.getFullName() + ",\n\n" +
                "Your patient profile has been updated by " + updatedBy + ".\n\n" +
                "Updated Profile Details:\n" +
                "Name: " + patient.getFullName() + "\n" +
                "Email: " + patient.getEmailAddress() + "\n" +
                "Phone: " + patient.getPhone() + "\n" +
                "Date of Birth: " + patient.getDateOfBirth() + "\n" +
                "Gender: " + patient.getGender() + "\n" +
                "Address: " + patient.getAddress() + "\n\n" +
                "Update Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If you have any questions about these changes, please contact our support team.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAdminRegistrationEmail(Admin admin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmail());
        message.setSubject("DocOnClick - Admin Account Created");
        message.setText("Dear " + admin.getFullName() + ",\n\n" +
                "Your admin account has been successfully created in DocOnClick.\n\n" +
                "Account Details:\n" +
                "Name: " + admin.getFullName() + "\n" +
                "Email: " + admin.getEmail() + "\n" +
                "Mobile: " + admin.getMobileNumber() + "\n" +
                "Role: " + admin.getRole() + "\n\n" +
                "Your account is currently inactive. Please wait for a super admin to activate your account.\n\n" +
                "Once activated, you'll have access to the admin dashboard.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAdminLoginEmail(Admin admin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmail());
        message.setSubject("DocOnClick - Admin Login Notification");
        message.setText("Dear " + admin.getFullName() + ",\n\n" +
                "You have successfully logged into the DocOnClick admin portal.\n\n" +
                "Login Time: " + java.time.LocalDateTime.now() + "\n" +
                "Role: " + admin.getRole() + "\n\n" +
                "If this wasn't you, please contact the system administrator immediately.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAdminActivationEmail(Admin admin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmail());
        message.setSubject("DocOnClick - Admin Account Activated");
        message.setText("Dear " + admin.getFullName() + ",\n\n" +
                "Great news! Your DocOnClick admin account has been activated.\n\n" +
                "You can now log in to the admin portal and access all administrative features.\n\n" +
                "Admin Portal Features:\n" +
                "- Manage doctor verifications\n" +
                "- View system analytics\n" +
                "- Manage user accounts\n" +
                "- Monitor appointments\n\n" +
                "Welcome to the DocOnClick admin team!\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAdminUpdateEmail(Admin admin, String updatedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(admin.getEmail());
        message.setSubject("DocOnClick - Admin Account Updated");
        message.setText("Dear " + admin.getFullName() + ",\n\n" +
                "Your DocOnClick admin account has been updated by " + updatedBy + ".\n\n" +
                "Updated Account Details:\n" +
                "Name: " + admin.getFullName() + "\n" +
                "Email: " + admin.getEmail() + "\n" +
                "Mobile: " + admin.getMobileNumber() + "\n" +
                "Role: " + admin.getRole() + "\n" +
                "Status: " + (admin.getActive() ? "Active" : "Inactive") + "\n\n" +
                "Update Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If you have any questions about these changes, please contact the system administrator.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAppointmentCompletedEmail(Appointment appointment) {
        // Send email to patient
        SimpleMailMessage patientMessage = new SimpleMailMessage();
        patientMessage.setTo(appointment.getPatient().getEmailAddress());
        patientMessage.setSubject("DocOnClick - Appointment Completed");
        patientMessage.setText("Dear " + appointment.getPatient().getFullName() + ",\n\n" +
                "Your appointment with Dr. " + appointment.getDoctor().getFullName() + 
                " has been marked as completed.\n\n" +
                "Appointment Details:\n" +
                "Doctor: Dr. " + appointment.getDoctor().getFullName() + "\n" +
                "Specialization: " + appointment.getDoctor().getSpecialization() + "\n" +
                "Date: " + appointment.getAppointmentDate() + "\n" +
                "Time: " + appointment.getAppointmentTime() + "\n" +
                "Status: Completed\n\n" +
                "Thank you for choosing DocOnClick for your healthcare needs.\n\n" +
                "You can now:\n" +
                "- View your prescription (if provided)\n" +
                "- Download appointment reports\n" +
                "- Rate and review the doctor\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(patientMessage);

        // Send email to doctor
        SimpleMailMessage doctorMessage = new SimpleMailMessage();
        doctorMessage.setTo(appointment.getDoctor().getEmail());
        doctorMessage.setSubject("DocOnClick - Appointment Completed Confirmation");
        doctorMessage.setText("Dear Dr. " + appointment.getDoctor().getFullName() + ",\n\n" +
                "You have successfully marked the appointment as completed.\n\n" +
                "Appointment Details:\n" +
                "Patient: " + appointment.getPatient().getFullName() + "\n" +
                "Date: " + appointment.getAppointmentDate() + "\n" +
                "Time: " + appointment.getAppointmentTime() + "\n" +
                "Status: Completed\n\n" +
                "The patient has been notified about the completion.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(doctorMessage);
    }

    @Async("taskExecutor")
    @Override
    public void sendAppointmentCancelledByDoctorEmail(Appointment appointment) {
        // Send email to patient
        SimpleMailMessage patientMessage = new SimpleMailMessage();
        patientMessage.setTo(appointment.getPatient().getEmailAddress());
        patientMessage.setSubject("DocOnClick - Appointment Cancelled by Doctor");
        patientMessage.setText("Dear " + appointment.getPatient().getFullName() + ",\n\n" +
                "We regret to inform you that your appointment with Dr. " + appointment.getDoctor().getFullName() + 
                " has been cancelled by the doctor.\n\n" +
                "Cancelled Appointment Details:\n" +
                "Doctor: Dr. " + appointment.getDoctor().getFullName() + "\n" +
                "Specialization: " + appointment.getDoctor().getSpecialization() + "\n" +
                "Date: " + appointment.getAppointmentDate() + "\n" +
                "Time: " + appointment.getAppointmentTime() + "\n" +
                "Status: Cancelled\n\n" +
                "We sincerely apologize for any inconvenience caused.\n\n" +
                "Next Steps:\n" +
                "- Your payment will be refunded within 3-5 business days\n" +
                "- You can book a new appointment with the same or different doctor\n" +
                "- Contact our support team if you need assistance\n\n" +
                "Support Email: support@doconclick.com\n" +
                "Support Phone: +1-800-DOC-CLICK\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(patientMessage);

        // Send confirmation email to doctor
        SimpleMailMessage doctorMessage = new SimpleMailMessage();
        doctorMessage.setTo(appointment.getDoctor().getEmail());
        doctorMessage.setSubject("DocOnClick - Appointment Cancellation Confirmation");
        doctorMessage.setText("Dear Dr. " + appointment.getDoctor().getFullName() + ",\n\n" +
                "You have successfully cancelled the appointment.\n\n" +
                "Cancelled Appointment Details:\n" +
                "Patient: " + appointment.getPatient().getFullName() + "\n" +
                "Date: " + appointment.getAppointmentDate() + "\n" +
                "Time: " + appointment.getAppointmentTime() + "\n" +
                "Status: Cancelled\n\n" +
                "The patient has been notified about the cancellation and will receive a refund.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(doctorMessage);
    }

    @Async("taskExecutor")
    @Override
    public void sendWelcomeEmail(String email, String fullName, String userType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to DocOnClick!");
        message.setText("Dear " + fullName + ",\n\n" +
                "Welcome to DocOnClick - Your trusted healthcare companion!\n\n" +
                "As a " + userType + ", you now have access to our comprehensive healthcare platform.\n\n" +
                "Get started today and experience seamless healthcare management.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAccountStatusEmail(String email, String fullName, String status, String userType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("DocOnClick - Account Status Update");
        message.setText("Dear " + fullName + ",\n\n" +
                "Your " + userType + " account status has been updated to: " + status + "\n\n" +
                "If you have any questions, please contact our support team.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }

    @Async("taskExecutor")
    @Override
    public void sendAccountUpdateNotificationEmail(String email, String fullName, String userType, String updatedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("DocOnClick - Account Updated");
        message.setText("Dear " + fullName + ",\n\n" +
                "Your " + userType + " account has been updated by " + updatedBy + ".\n\n" +
                "Update Time: " + java.time.LocalDateTime.now() + "\n\n" +
                "If you have any questions about these changes, please contact our support team.\n\n" +
                "Best regards,\n" +
                "DocOnClick Team");
        
        mailSender.send(message);
    }
}
