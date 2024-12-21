package com.project.workzone.dto.signIn;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String email;
    private String password;
}
