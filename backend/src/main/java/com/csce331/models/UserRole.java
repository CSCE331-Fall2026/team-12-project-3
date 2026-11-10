package com.csce331.models;

public enum UserRole {
    CUSTOMER("customer"),
    MANAGER("manager");

    private final String displayName;

    private UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return this.displayName;
    }

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
