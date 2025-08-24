package com.moidot.backend.user.repository;

import com.moidot.backend.auth.verify.SocialProvider;
import com.moidot.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByProviderUserIdAndProvider(String providerUserId, SocialProvider provider);

}
