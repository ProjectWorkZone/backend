package com.project.workzone.service;

import com.project.workzone.dto.signUp.SignUpRequest;
import com.project.workzone.model.Role;
import com.project.workzone.model.User;
import com.project.workzone.model.common.Gender;
import com.project.workzone.repository.RoleRepository;
import com.project.workzone.repository.UserRepository;
import com.project.workzone.util.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

//    public List<User> getUsersByIds(List<String> ids) {
//        return userRepository.findAllById(ids);
//    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public ResponseEntity<?> saveUser(SignUpRequest signUpRequest) {
        if (this.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .body("Error: Username is already taken!");

        }

//        if (userService.existsByEmail(signupRequest.getEmail())) {
//            return ResponseEntity
//                    .status(BAD_REQUEST)
//                    .contentType(APPLICATION_JSON)
//                    .body("Error: Email is already in use!");
//        }

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

        Role userRole = roleRepository.findRoleById(3L);

        user.setRoles(Collections.singleton(userRole));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Error: Unable to save user due to a database error."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public void updateTokens(Long id, String accessToken, String refreshToken) {
        userRepository.updateTokens(id, accessToken, refreshToken);
    }

    public void updateAccessToken(Long id, String accessToken) {
        userRepository.updateAccessToken(id, accessToken);
    }

    public void deleteTokens(Long id) {
        userRepository.deleteTokens(id);
    }
}
