package com.project.workzone.dto.signUp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private Long id;
    private String username;
    private String email;
}