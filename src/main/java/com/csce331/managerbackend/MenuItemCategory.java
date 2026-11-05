package com.csce331.managerbackend;

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
}
