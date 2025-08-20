package com.moidot.backend.tokentest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/token-test")
@Controller
public class HelloController {

    @GetMapping
    public Object hello() {
        return null;
    }
}
