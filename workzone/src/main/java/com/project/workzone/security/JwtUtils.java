package com.project.workzone.security;

import com.project.workzone.model.User;
import com.project.workzone.model.UsernameEmailPasswordAuthenticationToken;
import com.project.workzone.model.common.TokenType;
import com.project.workzone.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static com.project.workzone.model.common.TokenType.ACCESS_TOKEN;
import static com.project.workzone.model.common.TokenType.REFRESH_TOKEN;

@Component
public class JwtUtils {
    private final int accessTokenValidityInSeconds;
    private final int refreshTokenValidityInSeconds;
    private final SecretKey signingKey;
    public static final String TOKEN_TYPE = "token_type";

    private final UserService userService;


    public JwtUtils(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration.access}") int accessTokenValidityInSeconds, @Value("${jwt.expiration.refresh}") int refreshTokenValidityInSeconds, UserService userService) {
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.userService = userService;
    }

    public String generateToken(UsernameEmailPasswordAuthenticationToken authentication, TokenType tokenType) {
        return generateToken(
                authentication.getId(),
                authentication.getName(),
                tokenType
        );
    }

    public String generateToken(Long id, String userName, TokenType tokenType) {
        JwtBuilder jwts = Jwts.builder()
                .id(String.valueOf(id))
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()));

        if (tokenType.equals(ACCESS_TOKEN)) {
            jwts
                    .claims(Map.of(TOKEN_TYPE, ACCESS_TOKEN))
                    .expiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds))
                    .signWith(signingKey);
        } else {
            jwts
                    .claims(Map.of(TOKEN_TYPE, REFRESH_TOKEN))
                    .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds))
                    .signWith(signingKey);
        }
        return jwts.compact();
    }

    public UsernameEmailPasswordAuthenticationToken validateToken(String token, TokenType tokenType) {

        Claims jwtPayload = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!jwtPayload.get(TOKEN_TYPE).equals(tokenType.name())) {
            throw new IllegalArgumentException("Invalid JWT token type");
        }

        User user = userService.getById(Long.parseLong(jwtPayload.getId())).orElseThrow(
                () ->
                        new IllegalArgumentException("User not found"));

        String StoredToken = tokenType.equals(ACCESS_TOKEN) ? user.getAccessToken() : user.getRefreshToken();
        if (StoredToken == null || !StoredToken.equals(token)) {
            throw new JwtException("JWT token does not exist");
        }

        return new UsernameEmailPasswordAuthenticationToken(Long.valueOf(jwtPayload.getId()), jwtPayload.getSubject(), user.getRoles());
    }

}
