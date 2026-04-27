package com.marjane.ems.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User entity with role-based access control.
 * No inheritance - roles are managed via Role enum.
 * Supports multiple roles (ADMIN, TECHNICIAN, EMPLOYEE).
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_eid", columnList = "eid"),
    @Index(name = "idx_role", columnList = "role"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_username", columnList = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 20)
    private String eid; 

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;  // ADMIN, TECHNICIAN, EMPLOYEE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;  // ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, ARCHIVED

    @Column(length = 100)
    private String department;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Leave> leaves;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presence> presences;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Availability availability;

    @ManyToMany
    @JoinTable(
        name = "user_shift",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "shift_id")
    )
    private List<Shift> shifts;

    // Tickets created by this user (any role can create)
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Ticket> createdTickets;

    // Tickets assigned to this user (filtered by TECHNICIAN role)
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL)
    private List<Ticket> assignedTickets;

    // Leaves approved by this user (filtered by ADMIN role)
    @OneToMany(mappedBy = "approver", cascade = CascadeType.ALL)
    private List<Leave> approvedLeaves;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}