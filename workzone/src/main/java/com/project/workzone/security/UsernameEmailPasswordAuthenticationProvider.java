package com.project.workzone.security;

import com.project.workzone.model.User;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import com.project.workzone.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameEmailPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernameEmailPasswordAuthenticationToken authRequest = (UsernameEmailPasswordAuthenticationToken) authentication;
        String principal = (String) authRequest.getPrincipal();
        String credentials = (String) authRequest.getCredentials();
        boolean withUsername = authRequest.isWithUsername();

        User user;

        if (withUsername) {
            user = userService.getByUsername(principal).orElseThrow(
                    () -> new AuthenticationServiceException("Invalid username/email or password"));
        } else {
            user = userService.getByEmail(principal).orElseThrow(
                    () -> new AuthenticationServiceException("Invalid username/email or password"));
        }

        if (!passwordEncoder.matches(credentials, user.getPassword())) {
            throw new AuthenticationServiceException("Invalid username/email or password");
        }
        return new UsernameEmailPasswordAuthenticationToken(user.getId(), user.getUsername(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
       return UsernameEmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
