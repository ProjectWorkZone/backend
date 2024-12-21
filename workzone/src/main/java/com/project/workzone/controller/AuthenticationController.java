package com.project.workzone.controller;

import com.project.workzone.dto.signIn.SignInRequest;
import com.project.workzone.dto.signUp.SignUpRequest;
import com.project.workzone.dto.token.AccessTokenResponse;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import com.project.workzone.security.JwtUtils;
import com.project.workzone.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.project.workzone.constants.HTTPConstants.*;
import static com.project.workzone.model.common.TokenType.ACCESS_TOKEN;
import static com.project.workzone.model.common.TokenType.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${jwt.expiration.access}")
    private int accessTokenExpiration;
    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpiration;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping(SIGN_UP_URL)
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signupRequest) {
        return userService.saveUser(signupRequest);
    }

    @PostMapping(SIGN_IN_URL)
    public ResponseEntity<?> authenticateUser(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
            UsernameEmailPasswordAuthenticationToken authentication = (UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            String accessToken = jwtUtils.generateToken(authentication, ACCESS_TOKEN);
            String refreshToken = jwtUtils.generateToken(authentication, REFRESH_TOKEN);

            userService.updateTokens(authentication.getId(), accessToken, refreshToken);

            Cookie refreshTokenCookie = new Cookie(REFRESH_COOKIE, refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(refreshTokenExpiration);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new AccessTokenResponse(accessToken, accessTokenExpiration));
    }

    @GetMapping(REFRESH_TOKEN_URL)
    public ResponseEntity<?> refreshToken() {
        UsernameEmailPasswordAuthenticationToken authentication = (UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String accessToken = jwtUtils.generateToken(authentication, ACCESS_TOKEN);
        userService.updateAccessToken(authentication.getId(), accessToken);

        return ResponseEntity.ok(new AccessTokenResponse(accessToken, accessTokenExpiration));
    }

    @PostMapping(SIGN_OUT_URL)
    public ResponseEntity<?> logoutUser(HttpServletResponse response) throws IOException {
        Long userId = ((UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getId();
        userService.deleteTokens(userId);

        Cookie refreshTokenCookie = new Cookie(REFRESH_COOKIE, null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Sign out successful");
    }
}
