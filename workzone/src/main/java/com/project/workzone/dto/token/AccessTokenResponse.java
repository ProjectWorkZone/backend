package com.project.workzone.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;
    private long expiresIn;
}
