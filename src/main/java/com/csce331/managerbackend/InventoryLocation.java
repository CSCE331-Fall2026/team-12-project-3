package com.csce331.managerbackend;

/**
 * The storage location where an ingredient is kept in the inventory.
 *
 * @author Kedar Panchal
 */
public enum InventoryLocation {
    /** Frozen storage. */
    FREEZER("Freezer"),
    /** Refrigerated storage. */
    REFRIGERATOR("Refrigerator"),
    /** Dry pantry storage. */
    PANTRY("Pantry");

    /** The human-readable name of the location. */
    private final String location;
    
    /**
     * Constructs a new {@code InventoryLocation} with the specified human-readable name.
     *
     * @param location the display name for the location
     */
    private InventoryLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the human-readable name of this location (for example,
     * {@code "Freezer"}).
     *
     * @return the display name for the location
     */
    @Override
    public String toString() {
        return this.location;
    }
}
