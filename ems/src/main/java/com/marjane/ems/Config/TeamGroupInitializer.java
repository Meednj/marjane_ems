package com.marjane.ems.Config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.marjane.ems.Services.TeamGroupService;

@Component
public class TeamGroupInitializer implements ApplicationRunner {

    private final TeamGroupService teamGroupService;

    public TeamGroupInitializer(TeamGroupService teamGroupService) {
        this.teamGroupService = teamGroupService;
    }

    @Override
    public void run(ApplicationArguments args) {
        teamGroupService.initializeDefaultGroups();
    }
}