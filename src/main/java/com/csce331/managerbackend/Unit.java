package com.csce331.managerbackend;

/**
 * The unit of measurement used to quantify an ingredient.
 *
 * @author Kedar Panchal
 */
public enum Unit {
    /** Measurement in kilograms. */
    KILOGRAMS("kg"),
    /** Measurement in liters. */
    LITERS("L"),
    /** Measurement by individual item count. */
    ITEMS("");

    /** The human-readable abbreviation for the unit. */
    private final String unit;
    
    /**
     * Constructs a new {@code Unit} with the specified human-readable abbreviation.
     *
     * @param unit the display abbreviation for the unit
     */
    private Unit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns the human-readable abbreviation for this unit (for example,
     * {@code "kg"} or {@code "L"}).
     *
     * @return the display abbreviation for the unit
     */
    @Override
    public String toString() {
        return this.unit;
    }
}
