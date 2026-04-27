package com.marjane.ems.Entities;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

/**
 * Ticket entity for task/issue management.
 * References User directly - any user can create, but only TECHNICIAN roles
 * should be assigned as technician in business logic.
 */
@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_creator_id", columnList = "creator_id"),
    @Index(name = "idx_technician_id", columnList = "technician_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
public class Ticket {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;  // Any user can create

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private User technician;  // Filtered by TECHNICIAN role in service

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketHistory> history;
}