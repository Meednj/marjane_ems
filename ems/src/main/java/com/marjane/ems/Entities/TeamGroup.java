package com.marjane.ems.Entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_groups")
public class TeamGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private TeamGroupName name;

    @Column(name = "number_of_members", nullable = false)
    private Integer numberOfMembers = 0;

    @OneToMany(mappedBy = "teamGroup", fetch = FetchType.LAZY)
    private List<User> technicians = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void synchronizeMemberCount() {
        if (numberOfMembers == null) {
            numberOfMembers = 0;
        }
    }

}
