package com.lakshit.doconclick.controller;

import com.lakshit.doconclick.DTO.AdminLoginDTO;
import com.lakshit.doconclick.DTO.AdminRequestDTO;
import com.lakshit.doconclick.DTO.AdminResponseDTO;
import com.lakshit.doconclick.Service.IAdminService;
import com.lakshit.doconclick.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
//@CrossOrigin(origins = "http://127.0.0.1:5501")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminResponseDTO> createAdmin(
            @RequestPart("adminData") @Valid AdminRequestDTO adminRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            adminRequestDTO.setProfileImage(profileImage);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(adminRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AdminResponseDTO> login(@RequestBody @Valid AdminLoginDTO loginDTO) {
        return ResponseEntity.ok(adminService.login(loginDTO.getEmail(), loginDTO.getPassword()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AdminResponseDTO>> getAllAdmins(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllAdmins(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<AdminResponseDTO>> getAllActiveAdmins(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllActiveAdmins(pageable));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<AdminResponseDTO>> getAdminsByRole(@PathVariable Role role) {
        return ResponseEntity.ok(adminService.getAdminsByRole(role));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestPart("adminData") @Valid AdminRequestDTO adminRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            adminRequestDTO.setProfileImage(profileImage);
        }
        
        return ResponseEntity.ok(adminService.updateAdmin(id, adminRequestDTO));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AdminResponseDTO> activateAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.activateAdmin(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AdminResponseDTO> deactivateAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deactivateAdmin(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        return ResponseEntity.ok(adminService.existsByEmail(email));
    }

    @GetMapping("/check/mobile")
    public ResponseEntity<Boolean> checkMobileNumberExists(@RequestParam String mobileNumber) {
        return ResponseEntity.ok(adminService.existsByMobileNumber(mobileNumber));
    }

    // New search endpoints
    @GetMapping("/search/name")
    public ResponseEntity<List<AdminResponseDTO>> searchAdminsByName(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchAdminsByName(name));
    }

    @GetMapping("/search/name/active")
    public ResponseEntity<List<AdminResponseDTO>> searchActiveAdminsByName(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchActiveAdminsByName(name));
    }

    @GetMapping("/search/id/{adminId}")
    public ResponseEntity<AdminResponseDTO> searchAdminByAdminId(@PathVariable Long adminId) {
        return ResponseEntity.ok(adminService.searchAdminByAdminId(adminId));
    }

    @GetMapping("/search/id/{adminId}/active")
    public ResponseEntity<AdminResponseDTO> searchActiveAdminByAdminId(@PathVariable Long adminId) {
        return ResponseEntity.ok(adminService.searchActiveAdminByAdminId(adminId));
    }
}
