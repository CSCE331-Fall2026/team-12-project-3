package com.csce331.managerbackend;

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
}
