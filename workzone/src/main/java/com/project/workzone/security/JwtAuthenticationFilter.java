package com.project.workzone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import com.project.workzone.model.common.TokenType;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static com.project.workzone.constants.HTTPConstants.*;
import static com.project.workzone.model.common.TokenType.ACCESS_TOKEN;
import static com.project.workzone.model.common.TokenType.REFRESH_TOKEN;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer_";
    private static final Set<String> REFRESH_COOKIE_URL = Set.of(SIGN_OUT_URL, REFRESH_TOKEN_URL);


    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            if (isWhitelistedOrAuthenticated(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean isRefreshCookieUrl = REFRESH_COOKIE_URL.contains(request.getRequestURI());
            TokenType tokenType = isRefreshCookieUrl ? REFRESH_TOKEN : ACCESS_TOKEN;

            String token = getJwtFromRequest(request, isRefreshCookieUrl);

            UsernameEmailPasswordAuthenticationToken authentication = jwtUtils.validateToken(token, tokenType);

            if (getContext().getAuthentication() == null) {
                getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (AuthenticationException | JwtException failed) {
            handleUnsuccessfulAuthentication(request, response, failed);
        }
    }

    private void handleUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, RuntimeException failed) throws IOException {
        ResponseEntity<String> responseEntity = ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Error: " + failed.getMessage());

        response.setContentType(CONTENT_TYPE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    private String getJwtFromRequest(HttpServletRequest request, boolean isRefreshCookieUrl) {
        Cookie[] cookies = request.getCookies();

        if (isRefreshCookieUrl) {
            if (cookies != null) {
                return Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_COOKIE.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElseThrow(() -> new JwtException("Refresh token is missing"));
            }
        }

        String bearerToken = request.getHeader(AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }

        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> ACCESS_COOKIE.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(() -> new JwtException("Access token is missing"));
        }

        throw new JwtException("JWT token is missing");
    }

    private boolean isWhitelistedOrAuthenticated(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Authentication authentication = getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return true;
        }

        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Stream.of(WHITE_LIST_URL)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
