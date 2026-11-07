package com.csce331.models;

/**
 * The portion size of a menu item.
 *
 * @author Kedar Panchal
 */
public enum MenuItemSize {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large");

    private final String displayName;

    private MenuItemSize(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name used to show this size, such as {@code "small"}.
     *
     * @return the display name
     */
    @Override
    public String toString() {
        return this.displayName;
    }

    /**
     * Converts a size name, such as the one stored in the database, into the
     * matching size.
     *
     * @param displayName the size name, case-insensitive
     * @return the matching size
     * @throws IllegalArgumentException if no size has that name
     */
    public static MenuItemSize fromString(String displayName) {
        switch (displayName.toLowerCase()) {
            case "small" -> {
                return SMALL;
            }
            case "medium" -> {
                return MEDIUM;
            }
            case "large" -> {
                return LARGE;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid menu item size: " + displayName);
        }
    }
}
