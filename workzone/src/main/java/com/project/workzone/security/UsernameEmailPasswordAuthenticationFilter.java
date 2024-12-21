package com.project.workzone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.workzone.controller.AuthenticationController;
import com.project.workzone.dto.signIn.SignInRequest;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.workzone.constants.HTTPConstants.CONTENT_TYPE;
import static com.project.workzone.constants.HTTPConstants.SIGN_IN_URL;

@Component
public class UsernameEmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private AuthenticationController authenticationController;

    public UsernameEmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl(SIGN_IN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        SignInRequest signInRequest = extractSigninRequest(request);

        String username = signInRequest.getUsername();
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();

        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            throw new AuthenticationServiceException("Authentication failed: need both username or email.");
        }

        if (password == null || password.isEmpty()) {
            throw new AuthenticationServiceException("Authentication failed: need password.");
        }

        boolean withUsername = (username != null && !username.isEmpty());
        String principal = withUsername ? username : email;

        UsernameEmailPasswordAuthenticationToken authRequest = new UsernameEmailPasswordAuthenticationToken(principal, password, withUsername);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authResult);

        ResponseEntity<?> responseEntity = authenticationController.authenticateUser(null, response);

        response.setContentType(CONTENT_TYPE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        ResponseEntity<String> responseEntity = ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Error: " + failed.getMessage());

        response.setContentType(CONTENT_TYPE);
        response.setStatus(responseEntity.getStatusCode().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    private SignInRequest extractSigninRequest(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(request.getInputStream(), SignInRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication failed: unable to read request body.", e);
        }
    }
}
