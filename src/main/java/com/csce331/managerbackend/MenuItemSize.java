package com.csce331.managerbackend;

/**
 * The size of a menu item, which can be small, medium, or large.
 *
 * @author Kedar Panchal
 */
public enum MenuItemSize {
    /** The small size. */
    SMALL("small"),

    /** The medium size. */
    MEDIUM("medium"),

    /** The large size. */
    LARGE("large");

    /** The human-readable name of the size. */
    private final String displayName;
    
    /**
     * Constructs a new {@code MenuItemSize} with the specified human-readable name.
     *
     * @param displayName the display name for the size
     */
    private MenuItemSize(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of this size (for example, {@code "small"}).
     *
     * @return the display name for the size
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
