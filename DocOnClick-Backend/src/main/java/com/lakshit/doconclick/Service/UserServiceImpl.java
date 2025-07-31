package com.lakshit.doconclick.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.DTO.UserRequestDTO;
import com.lakshit.doconclick.DTO.UserResponseDTO;
import com.lakshit.doconclick.DTO.UserUpdateDTO;
import com.lakshit.doconclick.Repository.UserRepository;
import com.lakshit.doconclick.entity.User;
import com.lakshit.doconclick.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    	
    private final UserRepository userRepository;
    private final CloudinaryFileUploadService cloudinaryFileUploadService;
    private final IEmailService emailService; // Added email service

    @Override
    public UserResponseDTO signUpUser(UserRequestDTO userRequestDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if phone already exists
        if (userRepository.existsByPhone(userRequestDTO.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        String profileImageUrl = null;

        if (userRequestDTO.getProfileImage() != null && !userRequestDTO.getProfileImage().isEmpty()) {
            MultipartFile image = userRequestDTO.getProfileImage();

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

        User user = UserMapper.toEntity(userRequestDTO, profileImageUrl);
        User savedUser = userRepository.save(user);
        
        // Send registration email
        emailService.sendUserRegistrationEmail(savedUser);
        
        return UserMapper.toDTO(savedUser);
    }

    @Override
    public UserResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // Send login email
        emailService.sendUserLoginEmail(user);

        return UserMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(userRequestDTO.getEmail()) && 
            userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if phone is being changed and if it already exists
        if (!existingUser.getPhone().equals(userRequestDTO.getPhone()) && 
            userRepository.existsByPhone(userRequestDTO.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        String profileImageUrl = null;
        MultipartFile image = userRequestDTO.getProfileImage();
        
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("Profile image exceeds 2MB limit");
            }

            String contentType = image.getContentType();
            if (!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
                throw new RuntimeException("Only PNG and JPEG formats are supported");
            }

            // Delete old image if exists
            if (existingUser.getProfileImage() != null && !existingUser.getProfileImage().equals("default-profile-image-url")) {
                cloudinaryFileUploadService.deleteImage(existingUser.getProfileImage());
            }

            // Upload new image
            profileImageUrl = cloudinaryFileUploadService.uploadImage(image);
        }

        // Create UserUpdateDTO from UserRequestDTO
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .fullName(userRequestDTO.getFullName())
                .email(userRequestDTO.getEmail())
                .phone(userRequestDTO.getPhone())
                .address(userRequestDTO.getAddress())
                .password(userRequestDTO.getPassword())
                .gender(userRequestDTO.getGender())
                .dateOfBirth(userRequestDTO.getDateOfBirth())
                .profileImage(userRequestDTO.getProfileImage())
                .build();

        UserMapper.updateFromUpdateDTO(updateDTO, existingUser, profileImageUrl);
        User updatedUser = userRepository.save(existingUser);
        
        // Send update email
        emailService.sendUserUpdateEmail(updatedUser, "Admin");
        
        return UserMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete profile image from Cloudinary if it exists
        if (user.getProfileImage() != null && !user.getProfileImage().equals("default-profile-image-url")) {
            cloudinaryFileUploadService.deleteImage(user.getProfileImage());
        }

        userRepository.delete(user);
    }
}
