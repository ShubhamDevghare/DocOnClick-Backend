package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.AdminRequestDTO;
import com.lakshit.doconclick.DTO.AdminResponseDTO;
import com.lakshit.doconclick.Repository.AdminRepository;
import com.lakshit.doconclick.entity.Admin;
import com.lakshit.doconclick.enums.Role;
import com.lakshit.doconclick.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final CloudinaryFileUploadService cloudinaryService;
    private final IEmailService emailService; // Added email service

    @Override
    @Transactional
    public AdminResponseDTO createAdmin(AdminRequestDTO requestDTO) {
        // Check if email already exists
        if (adminRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if mobile number already exists
        if (adminRepository.existsByMobileNumber(requestDTO.getMobileNumber())) {
            throw new RuntimeException("Mobile number already exists");
        }
        
        String profileImageUrl = null;
        MultipartFile profileImage = requestDTO.getProfileImage();
        
        if (profileImage != null && !profileImage.isEmpty()) {
            // Validate image
            if (profileImage.getSize() > 2 * 1024 * 1024) { // 2MB limit
                throw new RuntimeException("Profile image exceeds 2MB limit");
            }
            
            String contentType = profileImage.getContentType();
            if (contentType == null || (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
                throw new RuntimeException("Only PNG and JPEG formats are supported");
            }
            
            // Upload to Cloudinary
            profileImageUrl = cloudinaryService.uploadImage(profileImage);
        }
        
        Admin admin = adminMapper.toEntity(requestDTO, profileImageUrl);
        // Set active as false during signup
        admin.setActive(false);
        Admin savedAdmin = adminRepository.save(admin);
        
        // Send registration email
        emailService.sendAdminRegistrationEmail(savedAdmin);
        
        return adminMapper.toDTO(savedAdmin);
    }

    @Override
    public AdminResponseDTO login(String email, String password) {
        // Only allow active admins to login
        Admin admin = adminRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or admin account is not active"));
        
        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        // Send login email
        emailService.sendAdminLoginEmail(admin);
        
        return adminMapper.toDTO(admin);
    }

    @Override
    @Transactional
    public AdminResponseDTO activateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        admin.setActive(true);
        Admin updatedAdmin = adminRepository.save(admin);
        
        // Send activation email
        emailService.sendAdminActivationEmail(updatedAdmin);
        
        return adminMapper.toDTO(updatedAdmin);
    }

    // ... rest of the methods remain the same ...
    
    @Override
    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        return adminMapper.toDTO(admin);
    }

    @Override
    public Page<AdminResponseDTO> getAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable)
                .map(adminMapper::toDTO);
    }

    @Override
    public Page<AdminResponseDTO> getAllActiveAdmins(Pageable pageable) {
        return adminRepository.findByActiveTrue(pageable)
                .map(adminMapper::toDTO);
    }

    @Override
    public List<AdminResponseDTO> getAdminsByRole(Role role) {
        return adminRepository.findByRole(role)
                .stream()
                .map(adminMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminResponseDTO updateAdmin(Long id, AdminRequestDTO requestDTO) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        // Check if email is being changed and if it already exists
        if (!existingAdmin.getEmail().equals(requestDTO.getEmail()) && 
            adminRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if mobile number is being changed and if it already exists
        if (!existingAdmin.getMobileNumber().equals(requestDTO.getMobileNumber()) && 
            adminRepository.existsByMobileNumber(requestDTO.getMobileNumber())) {
            throw new RuntimeException("Mobile number already exists");
        }
        
        String profileImageUrl = null;
        MultipartFile profileImage = requestDTO.getProfileImage();
        
        if (profileImage != null && !profileImage.isEmpty()) {
            // Validate image
            if (profileImage.getSize() > 2 * 1024 * 1024) { // 2MB limit
                throw new RuntimeException("Profile image exceeds 2MB limit");
            }
            
            String contentType = profileImage.getContentType();
            if (contentType == null || (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
                throw new RuntimeException("Only PNG and JPEG formats are supported");
            }
            
            // Delete old image if exists
            if (existingAdmin.getProfileImage() != null && !existingAdmin.getProfileImage().equals("default-admin-image-url")) {
                cloudinaryService.deleteImage(existingAdmin.getProfileImage());
            }
            
            // Upload new image
            profileImageUrl = cloudinaryService.uploadImage(profileImage);
        }
        
        adminMapper.updateEntityFromDTO(requestDTO, existingAdmin, profileImageUrl);
        Admin updatedAdmin = adminRepository.save(existingAdmin);
        
        return adminMapper.toDTO(updatedAdmin);
    }

    @Override
    @Transactional
    public AdminResponseDTO deactivateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        admin.setActive(false);
        Admin updatedAdmin = adminRepository.save(admin);
        
        return adminMapper.toDTO(updatedAdmin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        
        // Delete profile image from Cloudinary if it exists
        if (admin.getProfileImage() != null && !admin.getProfileImage().equals("default-admin-image-url")) {
            cloudinaryService.deleteImage(admin.getProfileImage());
        }
        
        adminRepository.delete(admin);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber) {
        return adminRepository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public List<AdminResponseDTO> searchAdminsByName(String name) {
        return adminRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(adminMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminResponseDTO> searchActiveAdminsByName(String name) {
        return adminRepository.findByFullNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(adminMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponseDTO searchAdminByAdminId(Long adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        return adminMapper.toDTO(admin);
    }

    @Override
    public AdminResponseDTO searchActiveAdminByAdminId(Long adminId) {
        Admin admin = adminRepository.findByAdminIdAndActiveTrue(adminId)
                .orElseThrow(() -> new RuntimeException("Active admin not found with id: " + adminId));
        
        return adminMapper.toDTO(admin);
    }
}
