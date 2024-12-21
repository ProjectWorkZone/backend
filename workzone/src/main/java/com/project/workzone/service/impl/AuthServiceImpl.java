package com.project.workzone.service.impl;

import com.project.workzone.dto.token.AccessTokenResponse;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import com.project.workzone.repository.UserRepository;
import com.project.workzone.security.JwtUtils;
import com.project.workzone.service.AuthService;
import com.project.workzone.util.ErrorResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.project.workzone.constants.HTTPConstants.REFRESH_COOKIE;
import static com.project.workzone.model.common.TokenType.ACCESS_TOKEN;
import static com.project.workzone.model.common.TokenType.REFRESH_TOKEN;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.expiration.access}")
    private int accessTokenExpiration;
    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpiration;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;


    @Override
    public ResponseEntity<?> loginUser(HttpServletResponse response) {
        try{
            UsernameEmailPasswordAuthenticationToken authentication = (UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            String accessToken = jwtUtils.generateToken(authentication, ACCESS_TOKEN);
            String refreshToken = jwtUtils.generateToken(authentication, REFRESH_TOKEN);

            userRepository.updateTokens(authentication.getId(), accessToken, refreshToken);

            Cookie refreshTokenCookie = new Cookie(REFRESH_COOKIE, refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(refreshTokenExpiration);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new AccessTokenResponse(accessToken, accessTokenExpiration));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> updateAccessToken() {
        try{
            UsernameEmailPasswordAuthenticationToken authentication = (UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            String accessToken = jwtUtils.generateToken(authentication, ACCESS_TOKEN);

            userRepository.updateAccessToken(authentication.getId(), accessToken);

            return ResponseEntity.ok(new AccessTokenResponse(accessToken, accessTokenExpiration));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> deleteTokens(HttpServletResponse response) {
        try{
            Long userId = ((UsernameEmailPasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getId();

            userRepository.deleteTokens(userId);

            Cookie refreshTokenCookie = new Cookie(REFRESH_COOKIE, null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok("Sign out successful");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
