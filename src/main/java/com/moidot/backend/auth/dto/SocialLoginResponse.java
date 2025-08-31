package com.moidot.backend.auth.dto;

import com.moidot.backend.auth.verify.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponse {

    private SocialProvider provider;
    private String email;

}
