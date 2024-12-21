package com.project.workzone.repository;

import com.project.workzone.model.User;

public interface CustomUserRepository {
    User updateUser(Long id, User user);
    void updateTokens(Long id, String accessToken, String refreshToken);
    void updateAccessToken(Long id, String accessToken);
    void deleteTokens(Long id);
}
