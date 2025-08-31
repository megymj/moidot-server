package com.moidot.backend.user.entity;

import com.moidot.backend.auth.verify.SocialProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@ToString
@Entity
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    // KAKAO, GOOGLE, NAVER 등
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public User (
            String providerUserId,
            SocialProvider provider,
            String email,
            Instant lastLoginAt, Instant createdAt, Instant updatedAt) {
        this.providerUserId = providerUserId;
        this.provider = provider;
        this.email = email;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {
        // 기본 생성자 필요 (JPA)
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public String getEmail() {
        return email;
    }
}
