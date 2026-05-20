package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marjane.ems.DAL.TeamGroupRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.response.TeamGroupResponse;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.UserStatus;
import com.marjane.ems.Factory.TeamGroupFactory;
import com.marjane.ems.Mapper.TeamGroupMapper;
import com.marjane.ems.Mapper.TechnicianMapper;

@Service
public class TeamGroupServiceImpl implements TeamGroupService {

    private final TeamGroupRepository teamGroupRepository;
    private final UserRepository userRepository;
    private final TeamGroupFactory teamGroupFactory;

    public TeamGroupServiceImpl(TeamGroupRepository teamGroupRepository,
            UserRepository userRepository,
            TeamGroupFactory teamGroupFactory) {
        this.teamGroupRepository = teamGroupRepository;
        this.userRepository = userRepository;
        this.teamGroupFactory = teamGroupFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamGroupResponse> getAllTeamGroups() {
        refreshAllGroupCounts();
        return teamGroupRepository.findAllByOrderByNameAsc().stream()
            .map(TeamGroupMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamGroupResponse> getTeamGroupByName(String name) {
        TeamGroupName groupName = TeamGroupName.valueOf(name.toUpperCase());
        return teamGroupRepository.findByName(groupName)
            .map(group -> {
                refreshGroupCount(groupName);
                return TeamGroupMapper.toResponse(group);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianResponse> getTechniciansByCategory(String category) {
        TeamGroupName groupName = TeamGroupName.valueOf(category.toUpperCase());
        return userRepository.findByTeamGroup_NameAndRoleAndStatus(groupName, Role.TECHNICIAN, UserStatus.ACTIVE)
            .stream()
            .map(TechnicianMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamGroup getTeamGroupEntity(TeamGroupName name) {
        return teamGroupRepository.findByName(name)
            .orElseGet(() -> teamGroupRepository.save(teamGroupFactory.createTeamGroup(name)));
    }

    @Override
    @Transactional
    public void initializeDefaultGroups() {
        for (TeamGroupName name : TeamGroupName.values()) {
            teamGroupRepository.findByName(name)
                .orElseGet(() -> teamGroupRepository.save(teamGroupFactory.createTeamGroup(name)));
        }

        refreshAllGroupCounts();
    }

    @Override
    @Transactional
    public void refreshAllGroupCounts() {
        for (TeamGroupName name : TeamGroupName.values()) {
            refreshGroupCount(name);
        }
    }

    @Override
    @Transactional
    public void refreshGroupCount(TeamGroupName name) {
        TeamGroup teamGroup = teamGroupRepository.findByName(name)
            .orElseGet(() -> teamGroupRepository.save(teamGroupFactory.createTeamGroup(name)));

        long count = userRepository.countByTeamGroup_NameAndRole(name, Role.TECHNICIAN);
        teamGroup.setNumberOfMembers((int) count);
        teamGroupRepository.save(teamGroup);
    }

    @Override
    @Transactional
    public TeamGroup assignTechnicianToGroup(User technician, TeamGroupName name) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        TeamGroup previousGroup = technician.getTeamGroup();
        TeamGroup newGroup = getTeamGroupEntity(name);
        technician.setTeamGroup(newGroup);
        User savedTechnician = userRepository.save(technician);

        if (previousGroup != null && previousGroup.getName() != null) {
            refreshGroupCount(previousGroup.getName());
        }

        refreshGroupCount(newGroup.getName());
        return savedTechnician.getTeamGroup();
    }
}