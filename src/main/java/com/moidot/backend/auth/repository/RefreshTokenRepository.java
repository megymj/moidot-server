package com.moidot.backend.auth.repository;

import com.moidot.backend.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findRefreshTokenBySessionIdAndToken(String sid, String token);

}
