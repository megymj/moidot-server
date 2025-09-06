package com.moidot.backend.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/sample")
@RestController
public class SampleController {

    @GetMapping
    public Object hello() {
        log.info("Sample Test API Called");
        return "Sample Test API Success!";
    }
}
