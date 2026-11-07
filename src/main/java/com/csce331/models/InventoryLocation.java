package com.csce331.models;

/**
 * Where an ingredient is stored in the inventory.
 *
 * @author Kedar Panchal
 */
public enum InventoryLocation {
    FREEZER("Freezer"),
    REFRIGERATOR("Refrigerator"),
    PANTRY("Pantry");

    private final String location;

    private InventoryLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the name used to show this location, such as {@code "Freezer"}.
     *
     * @return the display name
     */
    @Override
    public String toString() {
        return this.location;
    }
}
