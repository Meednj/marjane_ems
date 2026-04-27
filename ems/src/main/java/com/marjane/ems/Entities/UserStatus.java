package com.marjane.ems.Entities;


public enum UserStatus {
    ACTIVE("Active", true),
    INACTIVE("Inactive", false),
    ON_LEAVE("On Leave", true),
    SUSPENDED("Suspended", false),
    ARCHIVED("Archived", false);

    private final String displayName;
    private final boolean isActive;

    UserStatus(String displayName, boolean isActive) {
        this.displayName = displayName;
        this.isActive = isActive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return isActive;
    }
}
