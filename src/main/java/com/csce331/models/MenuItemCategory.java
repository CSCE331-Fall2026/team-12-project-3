package com.csce331.models;

/**
 * The category a menu item belongs to.
 *
 * @author Kedar Panchal
 */
public enum MenuItemCategory {
    SIDE("side"),
    ENTREE("entree"),
    DRINK("drink"),
    APPETIZER("appetizer");

    private final String displayName;

    private MenuItemCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name used to show this category, such as {@code "side"}.
     *
     * @return the display name
     */
    @Override
    public String toString() {
        return this.displayName;
    }

    /**
     * Converts a category name, such as the one stored in the database, into
     * the matching category.
     *
     * @param displayName the category name, case-insensitive
     * @return the matching category
     * @throws IllegalArgumentException if no category has that name
     */
    public static MenuItemCategory fromString(String displayName) {
        switch (displayName.toLowerCase()) {
            case "side" -> {
                return SIDE;
            }
            case "entree" -> {
                return ENTREE;
            }
            case "drink" -> {
                return DRINK;
            }
            case "appetizer" -> {
                return APPETIZER;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid menu item category: " + displayName);
        }
    }
}
