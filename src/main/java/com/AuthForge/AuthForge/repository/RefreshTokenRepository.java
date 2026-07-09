package com.AuthForge.AuthForge.repository;

import com.AuthForge.AuthForge.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>{

    Optional<RefreshToken> findByToken(String token);


    @Modifying
    @Query("UPDATE RefreshToken  r SET r.revoked=true WHERE r.user.id=:userID")
    void revokeAllByUserId(UUID userId);

}