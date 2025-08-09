package com.moidot.backend.auth.verify;

public record VerifiedIdentity(
        SocialProvider provider,
        String providerUserId,
        String email
) {}
