package com.marjane.ems.DAL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.TeamGroupName;

@Repository
public interface TeamGroupRepository extends JpaRepository<TeamGroup, Long> {
    Optional<TeamGroup> findByName(TeamGroupName name);

    List<TeamGroup> findAllByOrderByNameAsc();

    boolean existsByName(TeamGroupName name);
}