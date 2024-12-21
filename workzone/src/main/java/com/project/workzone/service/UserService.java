package com.project.workzone.service;

import com.project.workzone.model.User;
import com.project.workzone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
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

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
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
