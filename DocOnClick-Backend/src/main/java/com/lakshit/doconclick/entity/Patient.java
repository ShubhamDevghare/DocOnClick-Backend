package com.lakshit.doconclick.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.lakshit.doconclick.enums.Gender;
import com.lakshit.doconclick.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;
    
    @Column(nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(nullable = false)
    private String phone;
   
    @Column(nullable = false)
    private String emailAddress;
    
    @Column(nullable = false)
    private String address;
     
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // Add this relationship to fix the query issue
    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments = new HashSet<>();
}
