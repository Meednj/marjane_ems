package com.marjane.ems.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Leave request entity.
 * References User directly - any user can request, only ADMIN approves.
 */
@Entity
@Data
@Table(name = "leaves", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_approver_id", columnList = "approver_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Any user can request

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;  // Must be ADMIN role (filtered in service)

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LeaveType type;

    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LeaveStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LeaveStatus.PENDING;  // Default status
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
