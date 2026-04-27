package com.marjane.ems.Entities;

/**
 * Role enumeration for role-based access control.
 * Replaces inheritance-based design with a clean, scalable approach.
 */
public enum Role {
    ADMIN("A", "Administrator"),
    TECHNICIAN("T", "Technician"),
    EMPLOYEE("E", "Employee");

    private final String prefix;
    private final String displayName;

    Role(String prefix, String displayName) {
        this.prefix = prefix;
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDisplayName() {
        return displayName;
    }
}
