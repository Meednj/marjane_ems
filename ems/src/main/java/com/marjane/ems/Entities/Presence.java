package com.marjane.ems.Entities;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "presences")
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    private LocalDate presenceDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;

    @Enumerated(EnumType.STRING)
    private PresenceStatus status;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;
    
}
