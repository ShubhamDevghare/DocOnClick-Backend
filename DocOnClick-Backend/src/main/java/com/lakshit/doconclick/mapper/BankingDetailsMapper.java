package com.lakshit.doconclick.mapper;

import com.lakshit.doconclick.DTO.BankingDetailsRequestDTO;
import com.lakshit.doconclick.DTO.BankingDetailsResponseDTO;
import com.lakshit.doconclick.DTO.BankingDetailsUpdateDTO;
import com.lakshit.doconclick.entity.BankingDetails;
import com.lakshit.doconclick.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class BankingDetailsMapper {

    public BankingDetails toEntity(BankingDetailsRequestDTO dto, Doctor doctor) {
        if (dto == null) {
            return null;
        }

        return BankingDetails.builder()
                .doctor(doctor)
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .cifNumber(dto.getCifNumber())
                .upiId(dto.getUpiId())
                .build();
    }

    public BankingDetailsResponseDTO toResponseDTO(BankingDetails entity) {
        if (entity == null) {
            return null;
        }

        return BankingDetailsResponseDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getDoctorId())
                .doctorName(entity.getDoctor().getFullName())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .cifNumber(entity.getCifNumber())
                .upiId(entity.getUpiId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(BankingDetailsUpdateDTO dto, BankingDetails entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setBankName(dto.getBankName());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setCifNumber(dto.getCifNumber());
        entity.setUpiId(dto.getUpiId());
    }
}
