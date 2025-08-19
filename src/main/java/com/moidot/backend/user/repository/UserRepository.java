package com.moidot.backend.user.repository;

import com.moidot.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    User findUserByEmail(String email);

}
