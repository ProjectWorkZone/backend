package com.project.workzone.config;

import com.project.workzone.security.JwtAuthenticationFilter;
import com.project.workzone.security.JwtUtils;
import com.project.workzone.security.UsernameEmailPasswordAuthenticationFilter;
import com.project.workzone.security.UsernameEmailPasswordAuthenticationProvider;
import com.project.workzone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.project.workzone.constants.HTTPConstants.WHITE_LIST_URL;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomCorsConfiguration corsConfiguration;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new UsernameEmailPasswordAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils jwtUtils(@Value("${jwt.secret}") String secretKey,
                             @Value("${jwt.expiration.access}") int accessTokenValidityInSeconds,
                             @Value("${jwt.expiration.refresh}") int refreshTokenValidityInSeconds,
                             UserService userService) {
        return new JwtUtils(secretKey, accessTokenValidityInSeconds, refreshTokenValidityInSeconds, userService);
    }

    @Bean
    public UsernameEmailPasswordAuthenticationFilter usernameEmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new UsernameEmailPasswordAuthenticationFilter(authenticationManager);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   UsernameEmailPasswordAuthenticationFilter usernameEmailPasswordAuthenticationFilter) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfiguration))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionFixation().none()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterAt(usernameEmailPasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
