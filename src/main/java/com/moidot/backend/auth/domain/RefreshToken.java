package com.moidot.backend.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expiry_at", nullable = false)
    private Instant expiryAt;

    // 생성 시점 추적성 확보
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public RefreshToken() {
        // 기본 생성자 필요 (JPA)
    }

    public RefreshToken(Long userId, String sessionId, String token, Instant expiryAt, Instant createdAt) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.token = token;
        this.expiryAt = expiryAt;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return expiryAt.isBefore(Instant.now());
    }

}
