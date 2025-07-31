package com.lakshit.doconclick.Service;

import java.util.List;

import com.lakshit.doconclick.DTO.UserRequestDTO;
import com.lakshit.doconclick.DTO.UserResponseDTO;

public interface IUserService {
    UserResponseDTO signUpUser(UserRequestDTO userRequestDTO);
    UserResponseDTO login(String email, String password);
    UserResponseDTO getUserById(Long userId);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO);
    void deleteUser(Long userId);
}