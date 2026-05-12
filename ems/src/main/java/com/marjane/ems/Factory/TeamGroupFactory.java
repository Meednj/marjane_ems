package com.marjane.ems.Factory;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.TeamGroupName;


@Component
public class TeamGroupFactory {

    public TeamGroup createTeamGroup(TeamGroupName name) {
        TeamGroup teamGroup = new TeamGroup();
        teamGroup.setName(name);
        teamGroup.setNumberOfMembers(0);
        teamGroup.setTechnicians(new ArrayList<>());
        return teamGroup;
    }
}