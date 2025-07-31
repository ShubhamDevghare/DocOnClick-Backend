package com.lakshit.doconclick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lakshit.doconclick.DTO.UserLoginDTO;
import com.lakshit.doconclick.DTO.UserRequestDTO;
import com.lakshit.doconclick.DTO.UserResponseDTO;
import com.lakshit.doconclick.Service.IUserService;

@RestController
@RequestMapping("/api/v1/users")
//@CrossOrigin(origins = "http://127.0.0.1:5501") 
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> signUp(
            @RequestPart("userData") @Validated UserRequestDTO userRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            userRequestDTO.setProfileImage(profileImage);
        }
        return ResponseEntity.ok(userService.signUpUser(userRequestDTO));
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody @Validated UserLoginDTO loginDTO) {
        return ResponseEntity.ok(userService.login(loginDTO.getEmail(), loginDTO.getPassword()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestPart("userData") @Validated UserRequestDTO userRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        
        if (profileImage != null) {
            userRequestDTO.setProfileImage(profileImage);
        }
        return ResponseEntity.ok(userService.updateUser(id, userRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
