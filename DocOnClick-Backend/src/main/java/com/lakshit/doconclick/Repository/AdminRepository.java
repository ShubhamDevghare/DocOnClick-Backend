package com.lakshit.doconclick.Repository;

import com.lakshit.doconclick.entity.Admin;
import com.lakshit.doconclick.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    List<Admin> findByRole(Role role);
    
    // New methods for active admin functionality
    Optional<Admin> findByEmailAndActiveTrue(String email);
    List<Admin> findByActiveTrue();
    Page<Admin> findByActiveTrue(Pageable pageable);
    
    // Search admin by name
    @Query("SELECT a FROM Admin a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Admin> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT a FROM Admin a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :name, '%')) AND a.active = true")
    List<Admin> findByFullNameContainingIgnoreCaseAndActiveTrue(@Param("name") String name);
    
    // Search by admin ID
    Optional<Admin> findByAdminId(Long adminId);
    Optional<Admin> findByAdminIdAndActiveTrue(Long adminId);
}
