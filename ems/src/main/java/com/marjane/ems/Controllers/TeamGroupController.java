package com.marjane.ems.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marjane.ems.DTO.response.TeamGroupResponse;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Services.TeamGroupService;

@RestController
@RequestMapping("/api/team-groups")
public class TeamGroupController {

    private final TeamGroupService teamGroupService;

    public TeamGroupController(TeamGroupService teamGroupService) {
        this.teamGroupService = teamGroupService;
    }

    @GetMapping
    public ResponseEntity<List<TeamGroupResponse>> getAllTeamGroups() {
        return ResponseEntity.ok(teamGroupService.getAllTeamGroups());
    }

    @GetMapping("/{name}")
    public ResponseEntity<TeamGroupResponse> getTeamGroupByName(@PathVariable String name) {
        return teamGroupService.getTeamGroupByName(name)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}/technicians")
    public ResponseEntity<List<TechnicianResponse>> getTechniciansByCategory(@PathVariable String category) {
        return ResponseEntity.ok(teamGroupService.getTechniciansByCategory(category));
    }

    @GetMapping("/{name}/technicians")
    public ResponseEntity<List<TechnicianResponse>> getTechniciansByGroup(@PathVariable String name) {
        return ResponseEntity.ok(teamGroupService.getTechniciansByCategory(name));
    }
}