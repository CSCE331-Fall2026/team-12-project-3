package com.csce331.models;

/**
 * The role assigned to a user, which determines whether they use the customer
 * or manager side of the application.
 */
public enum UserRole {
    /** A customer placing orders. */
    CUSTOMER("customer"),
    /** A restaurant manager. */
    MANAGER("manager");

    private final String displayName;

    private UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name used to show this role, such as {@code "customer"}.
     *
     * @return the display name
     */
    public String toString() {
        return this.displayName;
    }

    /**
     * Converts a role name, such as the one stored in the database, into the
     * matching role.
     *
     * @param displayName the role name, case-insensitive
     * @return the matching role
     * @throws IllegalArgumentException if no role has that name
     */
    public static UserRole fromString(String displayName) {
        switch (displayName.toLowerCase()) {
            case "customer" -> {
                return CUSTOMER;
            }
            case "manager" -> {
                return MANAGER;
            }
            default -> throw new IllegalArgumentException("No role with name " + displayName);
        }
    }
}
