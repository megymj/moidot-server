package com.moidot.backend.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/token-test")
@RestController
public class HelloController {

    @GetMapping
    public Object hello() {
        return null;
    }
}
