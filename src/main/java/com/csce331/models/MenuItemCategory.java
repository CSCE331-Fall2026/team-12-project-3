package com.csce331.models;

/**
 * The category a menu item belongs to.
 *
 * @author Kedar Panchal
 */
public enum MenuItemCategory {
    /** A side dish, shown as {@code "side"}. */
    SIDE("side"),
    /** A main dish, shown as {@code "entree"}. */
    ENTREE("entree"),
    /** A beverage, shown as {@code "drink"}. */
    DRINK("drink"),
    /** An appetizer, shown as {@code "appetizer"}. */
    APPETIZER("appetizer");

    private final String displayName;

    /**
     * Creates a category with the given display name.
     *
     * @param displayName the name used in the database and in displays
     */
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
