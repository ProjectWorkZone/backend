package com.project.workzone.repository.impl;

import com.project.workzone.model.Role;
import com.project.workzone.model.User;
import com.project.workzone.repository.CustomUserRepository;
import com.project.workzone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRefreshToken(user.getRefreshToken());
        existingUser.setRoles(
                user.getAuthorities().stream()
                        .map(authority -> {
                            Role role = new Role(); // Создаем объект Role
                            role.setName(authority.getAuthority()); // Присваиваем имя роли
                            return role;
                        })
                        .collect(Collectors.toSet())
        );
        //        existingUser.setAccountNonExpired(user.isAccountNonExpired());
        //        existingUser.setAccountNonLocked(user.isAccountNonLocked());
//        existingUser.setCredentialsNonExpired(user.isCredentialsNonExpired());
        existingUser.setEnabled(user.isEnabled());

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void updateTokens(Long id, String accessToken, String refreshToken) {
        userRepository.updateTokens(id, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void updateAccessToken(Long id, String accessToken) {
        userRepository.updateAccessToken(id, accessToken);
    }

    @Override
    @Transactional
    public void deleteTokens(Long id) {
        userRepository.deleteTokens(id);
    }
}
