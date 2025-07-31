package com.lakshit.doconclick.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lakshit.doconclick.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
	
}
