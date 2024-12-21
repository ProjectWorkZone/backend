package com.project.workzone.service.impl;

import com.project.workzone.dto.signUp.SignUpRequest;
import com.project.workzone.model.Role;
import com.project.workzone.model.User;
import com.project.workzone.model.common.Gender;
import com.project.workzone.repository.RoleRepository;
import com.project.workzone.repository.UserRepository;
import com.project.workzone.service.UserService;
import com.project.workzone.util.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public ResponseEntity<?> saveUser(SignUpRequest signUpRequest) {
        try {
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(signUpRequest.getPassword());
            user.setGender(Gender.valueOf(String.valueOf(signUpRequest.getGender())));
            user.setAge(signUpRequest.getAge());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setLastName(signUpRequest.getLastName());
            user.setFirstName(signUpRequest.getFirstName());
            user.setStatus(User.Status.ACTIVE);
            user.setEnabled(true);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            user.setRoles(Collections.singleton(roleRepository.findRoleById(3L)));

            try {
                userRepository.save(user);
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("Error: Unable to save user due to a database error."));
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
