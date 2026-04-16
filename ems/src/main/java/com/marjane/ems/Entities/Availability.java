package com.marjane.ems.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Relation vers l'utilisateur (ou l'employé spécifiquement)
    @OneToOne // Souvent un utilisateur n'a qu'un seul état de disponibilité actuel
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
