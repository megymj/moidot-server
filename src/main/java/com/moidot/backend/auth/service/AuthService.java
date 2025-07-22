package com.moidot.backend.auth.service;

import com.moidot.backend.auth.dto.SocialLoginRequest;
import com.moidot.backend.auth.dto.SocialLoginResponse;
import com.moidot.backend.auth.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public SocialLoginResponse kakaoLogin(SocialLoginRequest request) {
        return handleSocialLogin(request);
    }

    private SocialLoginResponse handleSocialLogin(SocialLoginRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        User user;
//
//        if (userOptional.isPresent()) {
//            user = userOptional.get();
//        } else {
//            user = new User(request.getEmail(), request.getNickname(), request.getProfileImage());
//            userRepository.save(user);
//        }

        String email = request.getEmail();

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);
        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);

//        user.setRefreshToken(refreshToken);
//        userRepository.save(user);

        return new SocialLoginResponse(email, accessToken, refreshToken);
    }

}
