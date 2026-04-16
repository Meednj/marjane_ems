package com.marjane.ems.Entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "leaves")
public class Leave {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String EID;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveType type;
    private String motif;
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Administrator approver;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
