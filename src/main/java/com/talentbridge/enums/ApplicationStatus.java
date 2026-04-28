package com.talentbridge.enums;

public enum ApplicationStatus {
    APPLIED("Applied"),
    UNDER_REVIEW("Under Review"),
    SHORTLISTED("Shortlisted"),
    REJECTED("Rejected");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
