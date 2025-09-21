package com.moidot.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@ToString
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String code;
    private final int status;
    private final String message;
    private final String path;           //  동적으로 주입
    private final Instant timestamp;    //  동적으로 주입
}
