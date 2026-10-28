package com.csce331.managerbackend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import java.time.LocalDate;

/**
 * An ingredient tracked in the inventory, aggregating all of its restock
 * batches and exposing its total on-hand quantity. Instances are created
 * through the {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class Ingredient implements Restockable {
    /** The name of the ingredient. */
    private final String name;
    /** The unit of measurement for the ingredient. */
    private final Unit unit;
    /** The storage location of the ingredient. */
    private final InventoryLocation location;
    /** The shelf life of the ingredient in days. */
    private final int lifespan;
    /** The total on-hand quantity across all restock batches. */
    private int quantity;
    /** The quantity at or below which the ingredient should be restocked. */
    private int restockThreshold;
    /** The restock batches for this ingredient, keyed by restock date. */
    private Map<LocalDate, InventoryItem> inventoryItems;

    /**
     * Creates an ingredient from the given builder.
     *
     * @param builder the builder holding the ingredient's configuration
     */
    private Ingredient(Builder builder) {
        this.name = builder.name;
        this.unit = builder.unit;
        this.location = builder.location;
        this.lifespan = builder.lifespan;
        this.quantity = builder.quantity;
        this.restockThreshold = builder.restockThreshold;
        this.inventoryItems = new HashMap<>();
    }

    /**
     * Returns the name of the ingredient.
     *
     * @return the ingredient name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the unit of measurement for the ingredient.
     *
     * @return the unit
     */
    public Unit getUnit() {
        return this.unit;
    }

    /**
     * Returns the storage location of the ingredient.
     *
     * @return the inventory location
     */
    public InventoryLocation getLocation() {
        return this.location;
    }

    /**
     * Returns the shelf life of the ingredient in days.
     *
     * @return the lifespan in days
     */
    public int getLifespan() {
        return this.lifespan;
    }

    /**
     * Returns the total on-hand quantity across all restock batches.
     *
     * @return the total quantity
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * Returns the quantity at or below which the ingredient should be
     * restocked.
     *
     * @return the restock threshold
     */
    public int getRestockThreshold() {
        return this.restockThreshold;
    }

    /**
     * Returns the restock batches currently held for this ingredient.
     *
     * @return an immutable list of inventory items
     */
    public List<InventoryItem> getInventoryItems() {
        return List.copyOf(this.inventoryItems.values());
    }

    /**
     * Adds a restock batch to this ingredient, merging it with an existing
     * batch that shares the same restock date, and updates the total quantity.
     *
     * @param item the inventory item to add
     */
    public void addInventoryItem(InventoryItem item) {
        if (this.inventoryItems.containsKey(item.getRestockDate())) {
            InventoryItem existingItem = this.inventoryItems.get(item.getRestockDate());
            existingItem.updateQuantity(item.getQuantity());
            return;
        } else {
            this.inventoryItems.put(item.getRestockDate(), item);
        }
        this.quantity += item.getQuantity();
    }

    /**
     * Sets the quantity at or below which the ingredient should be restocked.
     *
     * @param threshold the new threshold; must be positive
     * @return a SQL query to update the restock threshold
     * @throws IllegalArgumentException if {@code threshold} is not positive
     */
    public String setRestockThreshold(int threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException("Restock threshold must be positive");
        }
        this.restockThreshold = threshold;
        return "UPDATE ingredients " +
            "SET restock_threshold = " + this.restockThreshold + " " +
            "WHERE name = '" + this.name + "';";
    }

    /**
     * Restocks this ingredient by the specified quantity, adding a new batch
     * dated today and updating the total quantity.
     *
     * @param quantity the quantity to restock; must be positive
     * @return a SQL query to insert or update the restock batch
     * @throws IllegalArgumentException if {@code quantity} is not positive
     */
    public String restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        InventoryItem item = new InventoryItem.Builder(this).quantity(quantity).build();
        this.addInventoryItem(item);
        return "INSERT INTO inventory (ingredient_id, quantity, restock_date) " +
            "VALUES SELECT id, " + quantity + ", CURRENT_DATE " +
            "FROM ingredients WHERE name = '" + this.name + "' " +
            "ON CONFLICT (ingredient_id, restock_date) " +
            "DO UPDATE SET quantity = inventory.quantity + " + quantity + ";";
    }

    /**
     * Returns whether the ingredient's total quantity has reached or fallen
     * below its restock threshold.
     *
     * @return {@code true} if restocking is needed, {@code false} otherwise
     */
    public boolean isRestockNeeded() {
        return this.quantity <= this.restockThreshold;
    }

    /**
     * Removes all expired restock batches from this ingredient and reduces the
     * total quantity accordingly.
     *
     * @return the list of inventory items that were removed
     */
    public List<InventoryItem> removeExpiredItems() {
        List<InventoryItem> expiredItems = this.inventoryItems
            .values()
            .stream()
            .filter(InventoryItem::isExpired)
            .toList();
        for (InventoryItem item : expiredItems) {
            this.inventoryItems.remove(item.getRestockDate());
            this.quantity -= item.getQuantity();
        }
        return expiredItems;
    }

    /**
     * Returns the inventory batch with the earliest restock date.
     *
     * @return the next expiring inventory item, or {@code null} if there are no batches
     */
    public InventoryItem getNextExpiringItem() {
        return this.inventoryItems
            .values()
            .stream()
            .min((a, b) -> a.getRestockDate().compareTo(b.getRestockDate()))
            .orElse(null);
    }

    /**
     * Builder for constructing {@link Ingredient} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        /** The name of the ingredient being built. */
        private final String name;
        /** The unit of measurement for the ingredient being built. */
        private final Unit unit;
        /** The storage location of the ingredient being built. */
        private final InventoryLocation location;
        /** The shelf life in days for the ingredient being built. */
        private int lifespan;
        /** The initial quantity for the ingredient being built. */
        private int quantity;
        /** The restock threshold for the ingredient being built. */
        private int restockThreshold;

        /**
         * Creates a builder for an ingredient with the given identity. The
         * lifespan defaults to zero and the restock threshold defaults to one.
         *
         * @param name the ingredient name
         * @param unit the unit of measurement
         * @param location the storage location
         */
        public Builder(String name, Unit unit, InventoryLocation location) {
            this.name = name;
            this.unit = unit;
            this.location = location;
            this.lifespan = 0;
            this.quantity = 0;
            this.restockThreshold = 1;
        }

        /**
         * Sets the shelf life of the ingredient in days.
         *
         * @param lifespan the lifespan in days
         * @return this builder
         */
        public Builder lifespan(int lifespan) {
            this.lifespan = lifespan;
            return this;
        }

        /**
         * Sets the quantity at or below which the ingredient should be
         * restocked.
         *
         * @param restockThreshold the restock threshold
         * @return this builder
         */
        public Builder restockThreshold(int restockThreshold) {
            this.restockThreshold = restockThreshold;
            return this;
        }

        /**
         * Builds the configured {@link Ingredient}.
         *
         * @return a new ingredient
         * @throws IllegalArgumentException if the name is empty, the unit or
         *         location is null, the lifespan is negative, or the restock
         *         threshold is not positive
         */
        public Ingredient build() {
            if (this.name == null || this.name.isEmpty()) {
                throw new IllegalArgumentException("Ingredient name must be specified");
            }

            if (this.unit == null) {
                throw new IllegalArgumentException("Ingredient unit must be specified");
            }

            if (this.location == null) {
                throw new IllegalArgumentException("Ingredient location must be specified");
            }

            if (this.lifespan < 0) {
                throw new IllegalArgumentException("Ingredient lifespan cannot be negative");
            }

            if (this.restockThreshold <= 0) {
                throw new IllegalArgumentException("Ingredient restock threshold must be positive");
            }

            return new Ingredient(this);
        }
    }
}
