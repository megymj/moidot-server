package com.moidot.backend.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/sample")
@RestController
public class SampleController {

    @GetMapping
    public Object hello() {
        return "Sample Test API Success!";
    }
}
