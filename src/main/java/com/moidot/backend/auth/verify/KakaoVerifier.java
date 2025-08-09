package com.moidot.backend.auth.verify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class KakaoVerifier implements SocialVerifier {

    private final RestTemplate rt;

    public KakaoVerifier(RestTemplate rt) {
        this.rt = rt;
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public VerifiedIdentity verify(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> req = new HttpEntity<>(headers);

        KakaoUserInfo body;
        try {
            ResponseEntity<KakaoUserInfo> resp = rt.exchange(url, HttpMethod.GET, req, KakaoUserInfo.class);
            body = resp.getBody();
        } catch (RestClientResponseException ex) {
            // 401/403 등은 UNAUTHORIZED로 매핑
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kakao token invalid: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Kakao API call failed", ex);
        }

        if (body == null || body.id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kakao verification failed");
        }

        String email = (body.kakaoAccount != null) ? body.kakaoAccount.email : null;

        return new VerifiedIdentity(SocialProvider.KAKAO, String.valueOf(body.id), email);
    }

    // ---- 필요한 필드만 매핑한 DTO ----
    @ToString
    static final class KakaoUserInfo {
        public Long id;
        @JsonProperty("kakao_account") public KakaoAccount kakaoAccount;
    }
    static final class KakaoAccount {
        public String email;
        public KakaoProfile profile;
    }
    static final class KakaoProfile {
        public String nickname;
        @JsonProperty("profile_image_url") public String profileImageUrl;
    }
}
