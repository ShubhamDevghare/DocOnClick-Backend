package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.UserRequestDTO;
import com.lakshit.doconclick.DTO.UserResponseDTO;
import com.lakshit.doconclick.DTO.UserUpdateDTO;
import com.lakshit.doconclick.entity.User;

public class UserMapper {

    private UserMapper() {
        // Prevent instantiation
    }

    public static User toEntity(UserRequestDTO dto, String profileImageUrl) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setProfileImage(profileImageUrl != null ? profileImageUrl : "default-profile-image-url");
        return user;
    }

    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDTO.builder()
            .userId(user.getUserId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .address(user.getAddress())
            .role(user.getRole())
            .gender(user.getGender())
            .dateOfBirth(user.getDateOfBirth())
            .profileImage(user.getProfileImage() != null ? user.getProfileImage() : "default-profile-image-url")
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
    
    // New method for updating from UserUpdateDTO
    public static void updateFromUpdateDTO(UserUpdateDTO dto, User user, String profileImageUrl) {
        if (dto == null || user == null) {
            return;
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        
        if (profileImageUrl != null) {
            user.setProfileImage(profileImageUrl);
        }
    }
}
