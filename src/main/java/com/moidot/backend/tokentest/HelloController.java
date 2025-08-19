package com.moidot.backend.tokentest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/token-test")
@Controller
public class HelloController {

    @PostMapping
    public void hello() {
        System.out.println("Hello, World!");
    }
}
