package com.csce331.models;

/**
 * The unit used to measure an ingredient.
 *
 * @author Kedar Panchal
 */
public enum Unit {
    /** Kilograms, shown as {@code "kg"}. */
    KILOGRAMS("kg"),
    /** Liters, shown as {@code "L"}. */
    LITERS("L"),
    /** Individual items, which have no abbreviation and show as an empty string. */
    ITEMS("");

    private final String unit;

    /**
     * Creates a unit with the given abbreviation.
     *
     * @param unit the abbreviation shown for the unit
     */
    private Unit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns the abbreviation for this unit. {@link #ITEMS} returns an empty
     * string because individual items are counted without one.
     *
     * @return the display abbreviation
     */
    @Override
    public String toString() {
        return this.unit;
    }
}
