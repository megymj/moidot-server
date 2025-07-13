package com.moidot.backend.auth.service;

import com.moidot.backend.auth.client.KakaoOAuthClient;
import com.moidot.backend.auth.dto.KakaoLoginResponse;
import com.moidot.backend.auth.dto.KakaoTokenResponse;
import com.moidot.backend.auth.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KakaoOAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
//    private final UserRepository userRepository;
//    private final JwtProvider jwtProvider;

    public KakaoLoginResponse loginWithKakao(String code) {
        KakaoTokenResponse tokenResponse = kakaoOAuthClient.getAccessToken(code);
        KakaoUserResponse userResponse = kakaoOAuthClient.getUserInfo(tokenResponse.getAccessToken());

        System.out.println("tokenResponse.toString() = " + tokenResponse.toString());
        System.out.println("userResponse = " + userResponse.toString());

//        User user = userRepository.findByKakaoId(userResponse.getId())
//                .orElseGet(() -> userRepository.save(User.fromKakao(userResponse)));

//        return jwtProvider.generateToken(user);
//        return tokenResponse.getAccessToken();
        return KakaoLoginResponse.from(tokenResponse, userResponse);
    }
}
