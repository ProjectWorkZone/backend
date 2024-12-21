package com.project.workzone.dto.signUp;

import com.project.workzone.model.common.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private int age;
    @NotBlank
    private Gender gender;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
}
