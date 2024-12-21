package com.project.workzone.controller;

import com.project.workzone.dto.signUp.SignUpRequest;
import com.project.workzone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.project.workzone.constants.HTTPConstants.SIGN_UP_URL;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(SIGN_UP_URL)
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        return userService.saveUser(signUpRequest);
    }
}