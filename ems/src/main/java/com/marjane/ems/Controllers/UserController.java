package com.marjane.ems.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.User;
import com.marjane.ems.auth.LoginRequest;
import com.marjane.ems.Services.UserService;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEid(request.eid())
                .or(() -> userRepository.findByUsername(request.eid()))
                .or(() -> userRepository.findByEmail(request.eid()))
                .orElse(null);

        if (user == null || !userService.verifyPassword(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok(Map.of(
                "role", user.getRole(),
                "message", "Login success"));
    }
    
    @GetMapping("/count")
    public Long getCount() {
        return userService.countAll();
    }
    
}
