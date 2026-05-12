package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;

import com.marjane.ems.DTO.response.TeamGroupResponse;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.User;

public interface TeamGroupService {
    List<TeamGroupResponse> getAllTeamGroups();

    Optional<TeamGroupResponse> getTeamGroupByName(String name);

    List<TechnicianResponse> getTechniciansByCategory(String category);

    TeamGroup getTeamGroupEntity(TeamGroupName name);

    void initializeDefaultGroups();

    void refreshAllGroupCounts();

    void refreshGroupCount(TeamGroupName name);

    TeamGroup assignTechnicianToGroup(User technician, TeamGroupName name);
}