package com.project.workzone.repository;

import com.project.workzone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.accessToken = :accessToken, u.refreshToken = :refreshToken WHERE u.id = :id")
    void updateTokens(@Param("id") Long id,
                      @Param("accessToken") String accessToken,
                      @Param("refreshToken") String refreshToken);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.accessToken = :accessToken WHERE u.id = :id")
    void updateAccessToken(@Param("id") Long id,
                           @Param("accessToken") String accessToken);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.accessToken = NULL, u.refreshToken = NULL WHERE u.id = :id")
    void deleteTokens(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
