# EMS Role-Based Design Analysis & Refactoring

## Executive Summary

**🚨 VERDICT: REMOVE INHERITANCE COMPLETELY**

Your current design is **over-engineered and semantically incorrect** for a role-based system. Roles are **permissions/metadata**, not **structural types**. Here's why:

---

## 1. Current Design Problems

### Problem 1: Semantic Confusion (Role vs. Type)

**What you have:**

```
User (abstract) ─── JOINED inheritance
├── Administrator
├── Employe
└── Technician
+ role: String (as metadata)
```

**Why this is wrong:**

- **OOP Inheritance** = "is-a" relationships for structural differences
  - Administrator "is-a" User with separate table
  - Technician "is-a" User with separate table
- **Role field** = "has-a" permission metadata
  - This is a contradiction!

**The semantic confusion:**

- If roles are structural (inheritance), why have a `role` String?
- If roles are metadata (String field), why have separate tables?

### Problem 2: Business Logic Coupled to Inheritance

**Current code in Ticket.java:**

```java
@ManyToOne
private Employe creator;  // ❌ Only Employees can create tickets?

@ManyToOne
private Technician technician;  // ❌ Only Technicians can be assigned?
```

**Problems:**

- What if an Administrator creates a ticket?
- What if someone's role changes from Technician to Employee?
- You'd need to recreate the Ticket row or add new columns
- Hard to implement flexible business rules

### Problem 3: EID Generation Issues

**Current approach:**

```java
this.setEID("A" + System.currentTimeMillis());  // ❌ Non-sequential, collision-prone
```

**Problems:**

- **Non-deterministic**: Uses current time, not a sequence
- **Collision risk**: In high concurrency, two threads might get the same timestamp
- **Non-readable**: "A1712345678901" is hard to reference verbally
- **Duplicate logic**: Same code repeated in 3 classes
- **Wrong place**: Business logic in @PrePersist is hard to test and maintain

### Problem 4: Query Performance

**JOINED Inheritance consequences:**

```sql
-- Even simple queries like "get all users" join multiple tables
SELECT * FROM users
LEFT JOIN administrators ON users.id = administrators.id
LEFT JOIN employes ON users.id = employes.id
LEFT JOIN technicians ON users.id = technicians.id
```

**Problems:**

- Unnecessary joins for every query
- Performance penalty as user count grows
- Complicates filtering/sorting
- Not justified by your use case

### Problem 5: Spring Security Incompatibility

**Spring Security expects:**

```java
public class User implements UserDetails {
    private String username;
    private String password;
    private Collection<GrantedAuthority> authorities;  // ← Roles here, not inheritance!
}
```

**Your design:**

- Uses inheritance instead of `GrantedAuthority`
- Spring Security's method-level security (`@PreAuthorize`) expects role names, not class types
- Extra mapping layer needed

---

## 2. Should You Keep Inheritance? ❌ NO

**Clear answer: REMOVE inheritance completely.**

| Aspect              | Should You Keep? | Why?                                 |
| ------------------- | ---------------- | ------------------------------------ |
| **Inheritance**     | ❌ NO            | Roles are metadata, not types        |
| **Multiple tables** | ❌ NO            | Single `users` table is simpler      |
| **Role as String**  | ⚠️ Maybe         | Use enum instead (type-safe)         |
| **EID generation**  | ✅ YES           | But move to service layer            |
| **Relationships**   | ✅ YES           | But reference `User`, not subclasses |

---

## 3. Recommended Design

### 3.1 Database Schema

```sql
-- Single users table (no inheritance)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eid VARCHAR(20) UNIQUE NOT NULL,  -- "E001", "T042", "A005"
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,  -- ADMIN, EMPLOYEE, TECHNICIAN
    status VARCHAR(20),
    department VARCHAR(100),  -- Only for EMPLOYEE
    activity_status VARCHAR(50),  -- Only for EMPLOYEE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (role),
    INDEX idx_eid (eid)
);

-- EID sequence table (for proper EID generation)
CREATE TABLE eid_sequences (
    role VARCHAR(20) PRIMARY KEY,
    next_value BIGINT DEFAULT 1,
    UNIQUE KEY unique_role (role)
);

-- Separate tables for relationships (no changes needed)
CREATE TABLE leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,  -- Can be any ADMIN
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
);

CREATE TABLE tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,  -- Can be any user
    technician_id BIGINT,  -- Can be any TECHNICIAN
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id),
    FOREIGN KEY (technician_id) REFERENCES users(id)
);
```

---

## 4. Refactored Entity Code

### 4.1 Role Enum (Type-Safe)

```java
package com.marjane.ems.Entities;

public enum Role {
    ADMIN("A"),
    TECHNICIAN("T"),
    EMPLOYEE("E");

    private final String prefix;

    Role(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
```

### 4.2 Single User Entity (Clean & Simple)

```java
package com.marjane.ems.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String eid;  // "E001", "T042", "A005"

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String status;

    // Employee-specific fields (nullable for other roles)
    private String department;
    private String activityStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Leave> leaves;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Presence> presences;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Availability availability;

    @ManyToMany
    @JoinTable(
        name = "user_shift",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "shift_id")
    )
    private List<Shift> shifts;

    // Tickets created by this user
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Ticket> createdTickets;

    // Tickets assigned to this user (if technician)
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL)
    private List<Ticket> assignedTickets;

    // Leaves approved by this user (if admin)
    @OneToMany(mappedBy = "approver", cascade = CascadeType.ALL)
    private List<Leave> approvedLeaves;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### 4.3 EID Generator Service (Proper Implementation)

```java
package com.marjane.ems.Services;

import com.marjane.ems.Entities.Role;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class EIDGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Generates a sequential EID with role prefix
     * Example: E001, E002, T001, A001, etc.
     */
    @Transactional
    public String generateEID(Role role) {
        // Get or create the sequence for this role
        String sql = "INSERT INTO eid_sequences (role, next_value) VALUES (?, 1) " +
                     "ON DUPLICATE KEY UPDATE next_value = LAST_INSERT_ID(next_value + 1)";

        // Simpler approach using native query
        entityManager.createNativeQuery(
            "UPDATE eid_sequences SET next_value = LAST_INSERT_ID(next_value + 1) WHERE role = ?"
        ).setParameter(1, role.name()).executeUpdate();

        Long nextValue = (Long) entityManager.createNativeQuery(
            "SELECT LAST_INSERT_ID()"
        ).getSingleResult();

        if (nextValue == null || nextValue == 0) {
            nextValue = 1L;
        }

        // Format: "E001", "T042", "A123"
        return String.format("%s%03d", role.getPrefix(), nextValue);
    }
}
```

**Alternative: Even simpler (if sequential IDs not critical):**

```java
@Service
public class EIDGeneratorService {
    @Autowired
    private UserRepository userRepository;

    public String generateEID(Role role) {
        // Count existing users with this role
        long count = userRepository.countByRole(role);
        return String.format("%s%03d", role.getPrefix(), count + 1);
    }
}
```

### 4.4 User Registration Service

```java
package com.marjane.ems.Services;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EIDGeneratorService eidGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String password,
                            Role role, String firstName, String lastName) {
        // Validate
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // Generate EID BEFORE saving
        user.setEid(eidGeneratorService.generateEID(role));

        return userRepository.save(user);
    }
}
```

### 4.5 Ticket Entity (Updated)

```java
package com.marjane.ems.Entities;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private User creator;  // ✅ Any user can create

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private User technician;  // ✅ Any user (filtered by role in business logic)

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "created_at", updatable = false)
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
}
```

### 4.6 Leave Entity (Updated)

```java
package com.marjane.ems.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
@Data
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;  // ✅ Any user can request leave

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;  // ✅ Any user (filtered by ADMIN role in service)

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType type;

    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 4.7 Repository

```java
package com.marjane.ems.Repositories;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEid(String eid);
    boolean existsByUsername(String username);
    List<User> findByRole(Role role);
    long countByRole(Role role);
}
```

---

## 5. Spring Security Integration

### 5.1 UserDetails Implementation

```java
package com.marjane.ems.Services;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ Roles become GrantedAuthorities here
        return Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(user.getStatus());
    }

    public User getUser() {
        return user;
    }
}
```

### 5.2 UserDetailsService Implementation

```java
package com.marjane.ems.Services;

import com.marjane.ems.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
            .findByUsername(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

### 5.3 Security Configuration

```java
package com.marjane.ems.Config;

import com.marjane.ems.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .requestMatchers("/technician/**").hasRole("TECHNICIAN")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 5.4 Controller Examples

```java
package com.marjane.ems.Controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    // ✅ Any authenticated user can view their own tickets
    @GetMapping
    public List<Ticket> getMyTickets() {
        // Get current user from SecurityContext
        return ticketService.getUserTickets(getCurrentUser());
    }

    // ✅ Only ADMINs can approve
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Ticket approveTicket(@PathVariable Long id) {
        return ticketService.approveTicket(id);
    }

    // ✅ Only TECHNICIANs can accept tickets
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public Ticket acceptTicket(@PathVariable Long id) {
        return ticketService.acceptTicket(id, getCurrentUser());
    }
}

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    // ✅ Only ADMINs can approve leaves
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Leave approveLeave(@PathVariable Long id) {
        return leaveService.approveLeave(id, getCurrentUser());
    }

    // ✅ Only ADMINs can view all leaves
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Leave> getAllLeaves() {
        return leaveService.getAllLeaves();
    }
}
```

---

## 6. Migration Path

### Step 1: Create new User table (single)

```sql
-- Create new unified table
CREATE TABLE users_new (
    -- [see schema above]
);

-- Create EID sequence table
CREATE TABLE eid_sequences (
    role VARCHAR(20) PRIMARY KEY,
    next_value BIGINT DEFAULT 1
);
```

### Step 2: Migrate data

```sql
INSERT INTO users_new (
    id, eid, username, email, password, first_name,
    last_name, phone, role, status, department,
    activity_status, created_at, updated_at
)
SELECT
    u.id,
    u.eid,
    u.username,
    u.email,
    u.password,
    u.first_name,
    u.last_name,
    u.phone,
    u.role,  -- Already a string
    u.status,
    e.department,
    e.activity_status,
    u.created_at,
    u.updated_at
FROM users u
LEFT JOIN employes e ON u.id = e.id;

-- Migrate admin/technician data similarly
```

### Step 3: Update foreign keys

```sql
-- Tickets: both creator and technician point to users_new
-- Leaves: both user and approver point to users_new
ALTER TABLE tickets MODIFY COLUMN creator_id BIGINT NOT NULL;
ALTER TABLE tickets MODIFY COLUMN technician_id BIGINT;

ALTER TABLE leaves MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE leaves MODIFY COLUMN approver_id BIGINT;
```

### Step 4: Drop old tables

```sql
DROP TABLE technicians;
DROP TABLE employes;
DROP TABLE administrators;
DROP TABLE users;

RENAME TABLE users_new TO users;
```

### Step 5: Update code

- Delete `Administrator.java`, `Employe.java`, `Technician.java`
- Update `User.java` to non-abstract
- Add `EIDGeneratorService`
- Update Spring Security config
- Update repositories and services

---

## 7. Key Differences Summary

| Aspect                | ❌ Current (Inheritance)                        | ✅ Recommended (Single Table + Enum) |
| --------------------- | ----------------------------------------------- | ------------------------------------ |
| **Tables**            | users + administrators + employes + technicians | users (only)                         |
| **Role Storage**      | Both inheritance + String field                 | Enum field (type-safe)               |
| **EID Generation**    | Repeated logic in 3 @PrePersist methods         | Single service, proper sequence      |
| **Ticket Creator**    | Only `Employe`                                  | Any `User` (filtered by role)        |
| **Role Change**       | Move rows between tables (impossible)           | Update role field (1 query)          |
| **Query Performance** | Joins 4 tables                                  | Single table                         |
| **Spring Security**   | Requires mapping                                | Direct GrantedAuthority              |
| **Flexibility**       | Hard to add new role                            | Add Role enum value                  |
| **Scalability**       | Limited (new role = new table)                  | Infinite (just add role to enum)     |

---

## 8. Final Checklist

- [ ] Delete inheritance-based entity classes
- [ ] Create new single `User` entity
- [ ] Create `Role` enum
- [ ] Create `EIDGeneratorService`
- [ ] Update repositories to reference `User` only
- [ ] Migrate database schema
- [ ] Implement `CustomUserDetails`
- [ ] Implement `CustomUserDetailsService`
- [ ] Update `SecurityConfig`
- [ ] Update all controllers to use `@PreAuthorize`
- [ ] Test JWT flow with different roles
- [ ] Test role-based access control

---

## Conclusion

**Your current design is over-engineered because:**

1. You're using inheritance for roles (wrong semantic)
2. You're mixing inheritance with a String role field (contradictory)
3. Business logic is tightly coupled to entity types (inflexible)
4. EID generation is duplicated and incorrect (non-deterministic)

**The fix is simple:**

- Single `users` table
- `Role` enum (ADMIN, TECHNICIAN, EMPLOYEE)
- Service-based EID generation
- All relationships reference `User`
- Spring Security handles permissions via `GrantedAuthority`

**This is production-grade because:**

- ✅ Clean separation: structure vs. permissions
- ✅ Scalable: new role = add enum value
- ✅ Flexible: change roles without data migration
- ✅ Secure: Spring Security best practices
- ✅ Performant: single table, no unnecessary joins
- ✅ Maintainable: less code, single source of truth
