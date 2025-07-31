package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.AdminRequestDTO;
import com.lakshit.doconclick.DTO.AdminResponseDTO;
import com.lakshit.doconclick.entity.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequestDTO dto, String profileImageUrl) {
        if (dto == null) {
            return null;
        }

        return Admin.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .mobileNumber(dto.getMobileNumber())
                .profileImage(profileImageUrl != null ? profileImageUrl : "default-admin-image-url")
                .role(dto.getRole())
                .active(false) // Default to false during signup
                .build();
    }

    public AdminResponseDTO toDTO(Admin entity) {
        if (entity == null) {
            return null;
        }

        return AdminResponseDTO.builder()
                .adminId(entity.getAdminId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .mobileNumber(entity.getMobileNumber())
                .profileImage(entity.getProfileImage())
                .role(entity.getRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(AdminRequestDTO dto, Admin entity, String profileImageUrl) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(dto.getPassword());
        }
        
        entity.setMobileNumber(dto.getMobileNumber());
        
        if (profileImageUrl != null) {
            entity.setProfileImage(profileImageUrl);
        }
        
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
        
        // Update active status if provided
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
