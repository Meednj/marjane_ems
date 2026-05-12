package com.marjane.ems.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import org.springframework.web.server.ResponseStatusException;

import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Mapper.TechnicianMapper;
import com.marjane.ems.Services.TeamGroupService;
import com.marjane.ems.Services.UserService;
import com.marjane.ems.Entities.UserStatus;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamGroupService teamGroupService;
    
    
    @GetMapping("/count")
    public Long getCount() {
        return userService.countAll();
    }

    @PutMapping("/me/status/{status}")
    public ResponseEntity<Void> updateCurrentUserStatus(Principal principal, @PathVariable String status) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }

        userService.getUserByEid(principal.getName())
            .ifPresentOrElse(
                user -> {
                    if (user.getStatus() == UserStatus.ON_LEAVE) {
                        throw new ResponseStatusException(HttpStatus.LOCKED, "Status is locked while the user is on leave");
                    }

                    userService.updateStatus(user.getId(), status);
                },
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found");
                }
            );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @PathVariable String status) {
        userService.updateStatus(id, status);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/role/technician")
    public ResponseEntity<TechnicianResponse> promoteToTechnician(
            @PathVariable Long id,
            @RequestParam String teamGroup) {
        User technician = userService.changeRole(id, Role.TECHNICIAN);
        teamGroupService.assignTechnicianToGroup(
                technician,
                TeamGroupName.valueOf(teamGroup.toUpperCase())
        );

        User refreshedTechnician = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(TechnicianMapper.toResponse(refreshedTechnician));
    }

    @GetMapping("/me")
    public ResponseEntity<TechnicianResponse> getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }

        return userService.getUserByEid(principal.getName())
            .map(TechnicianMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    }
    
}
