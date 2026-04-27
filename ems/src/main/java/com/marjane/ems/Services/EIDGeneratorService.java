package com.marjane.ems.Services;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

/**
 * Service for generating sequential EIDs (Employee IDs).
 * Ensures unique, sequential IDs with role-based prefixes.
 * Example: E001, E002, T001, A001, etc.
 */
@Service
public class EIDGeneratorService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Generates the next sequential EID for a given role.
     * Format: {ROLE_PREFIX}{3-digit-number}
     * 
     * @param role The role for which to generate EID
     * @return Sequential EID (e.g., "E001", "T042", "A005")
     */
    @Transactional
    public String generateEID(Role role) {
        // Count existing users with this role
        long count = countUsersByRole(role);
        long nextNumber = count + 1;
        
        // Format: E001, T042, A005, etc.
        return String.format("%s%03d", role.getPrefix(), nextNumber);
    }

    /**
     * Counts users by role.
     * @param role The role to count
     * @return Number of users with this role
     */
    public long countUsersByRole(Role role) {
        return userRepository.findByRole(role).size();
    }

    /**
     * Checks if an EID already exists in the database.
     * @param eid The EID to check
     * @return true if EID exists, false otherwise
     */
    public boolean eidExists(String eid) {
        return userRepository.findByEid(eid).isPresent();
    }

    /**
     * Validates EID format and role consistency.
     * @param eid The EID to validate
     * @param role The expected role
     * @return true if EID matches role prefix, false otherwise
     */
    public boolean validateEIDForRole(String eid, Role role) {
        return eid != null && eid.startsWith(role.getPrefix());
    }
}
