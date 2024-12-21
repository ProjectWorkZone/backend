package com.project.workzone.service;

import com.project.workzone.dto.signUp.SignUpRequest;
import com.project.workzone.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);
    ResponseEntity<?> saveUser(SignUpRequest signUpRequest);
}
