package com.project.workzone.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> loginUser(HttpServletResponse response);
    ResponseEntity<?> updateAccessToken();
    ResponseEntity<?> deleteTokens(HttpServletResponse response);
}
