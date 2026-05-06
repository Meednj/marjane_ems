package com.marjane.ems.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marjane.ems.Services.UserService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    
    @GetMapping("/count")
    public Long getCount() {
        return userService.countAll();
    }
    
}
