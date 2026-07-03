package com.AuthForge.AuthForge.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="refresh_tokens")
public class RefreshToken {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false,unique=true)
    private String token;

    @Column(nullable=false)
    private LocalDateTime expiresAt;


    private LocalDateTime createdAt;

    @Column(nullable=false)
    private boolean revoked;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
        this.revoked=false;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    public boolean isValid(){
        return !revoked && !isExpired();
    }
}
