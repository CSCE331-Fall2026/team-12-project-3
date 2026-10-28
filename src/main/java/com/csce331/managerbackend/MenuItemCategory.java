package com.csce331.managerbackend;

/**
 * The category of a menu item, which can be a side, entree, drink, or appetizer.
 *
 * @author Kedar Panchal
 */
public enum MenuItemCategory {
    /** The side category. */
    SIDE("side"),

    /** The entree category. */
    ENTREE("entree"),

    /** The drink category. */
    DRINK("drink"),

    /** The appetizer category. */
    APPETIZER("appetizer");

    /** The human-readable name of the category. */
    private final String displayName;
    
    /**
     * Constructs a new {@code MenuItemCategory} with the specified human-readable name.
     *
     * @param displayName the display name for the category
     */
    private MenuItemCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of this category (for example, {@code "side"}).
     *
     * @return the display name for the category
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
