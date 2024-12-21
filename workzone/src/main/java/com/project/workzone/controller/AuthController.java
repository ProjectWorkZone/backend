package com.project.workzone.controller;

import com.project.workzone.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.workzone.constants.HTTPConstants.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(SIGN_IN_URL)
    public ResponseEntity<?> authenticateUser(HttpServletResponse response) {
           return authService.loginUser(response);
    }

    @GetMapping(REFRESH_TOKEN_URL)
    public ResponseEntity<?> refreshToken() {
        return authService.updateAccessToken();
    }

    @PostMapping(SIGN_OUT_URL)
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        return authService.deleteTokens(response);
    }
}
