package com.lakshit.doconclick.Service;

import com.lakshit.doconclick.DTO.AdminRequestDTO;
import com.lakshit.doconclick.DTO.AdminResponseDTO;
import com.lakshit.doconclick.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminService {
    
    AdminResponseDTO createAdmin(AdminRequestDTO requestDTO);
    
    AdminResponseDTO login(String email, String password);
    
    AdminResponseDTO getAdminById(Long id);
    
    Page<AdminResponseDTO> getAllAdmins(Pageable pageable);
    
    Page<AdminResponseDTO> getAllActiveAdmins(Pageable pageable);
    
    List<AdminResponseDTO> getAdminsByRole(Role role);
    
    AdminResponseDTO updateAdmin(Long id, AdminRequestDTO requestDTO);
    
    AdminResponseDTO activateAdmin(Long id);
    
    AdminResponseDTO deactivateAdmin(Long id);
    
    void deleteAdmin(Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    // New search methods
    List<AdminResponseDTO> searchAdminsByName(String name);
    
    List<AdminResponseDTO> searchActiveAdminsByName(String name);
    
    AdminResponseDTO searchAdminByAdminId(Long adminId);
    
    AdminResponseDTO searchActiveAdminByAdminId(Long adminId);
}
