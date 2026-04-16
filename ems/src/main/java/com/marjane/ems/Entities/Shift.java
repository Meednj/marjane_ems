package com.marjane.ems.Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "shifts")
public class Shift {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shift_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateShift;

    @Column(name = "shift_start_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime starTime;

    @Column(name = "shift_end_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @OneToMany(mappedBy = "shift")
    private List<Presence> presences;

    @PrePersist
    protected void onCreate() {
        dateShift = LocalDate.now();
        starTime = LocalTime.now();
    }

    @ManyToMany(mappedBy = "shifts")
    private List<User> users;

}
