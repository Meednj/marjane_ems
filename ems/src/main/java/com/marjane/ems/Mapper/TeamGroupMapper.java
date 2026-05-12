package com.marjane.ems.Mapper;

import java.util.List;

import com.marjane.ems.DTO.request.TeamGroupRequest;
import com.marjane.ems.DTO.response.TeamGroupResponse;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.TeamGroupName;

public class TeamGroupMapper {

    public static TeamGroupResponse toResponse(TeamGroup teamGroup) {
        if (teamGroup == null) {
            throw new IllegalArgumentException("TeamGroup cannot be null");
        }

        List<TechnicianResponse> technicians = teamGroup.getTechnicians() == null
            ? List.of()
            : teamGroup.getTechnicians().stream().map(TechnicianMapper::toResponse).toList();

        return new TeamGroupResponse(
            teamGroup.getId(),
            teamGroup.getName() != null ? teamGroup.getName().name() : null,
            teamGroup.getNumberOfMembers(),
            technicians
        );
    }

    public static TeamGroup toEntity(TeamGroupRequest request) {
        if (request == null) {
            return null;
        }

        TeamGroup teamGroup = new TeamGroup();
        if (request.name() != null) {
            teamGroup.setName(TeamGroupName.valueOf(request.name().toUpperCase()));
        }
        return teamGroup;
    }
}