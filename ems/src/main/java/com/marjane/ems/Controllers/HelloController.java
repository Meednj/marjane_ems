package com.marjane.ems.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // This is the prefix
public class HelloController {

    @GetMapping("/hello") // This is the specific endpoint
    public String sayHello() {
        return "Hello from Spring Boot!";
    }
}