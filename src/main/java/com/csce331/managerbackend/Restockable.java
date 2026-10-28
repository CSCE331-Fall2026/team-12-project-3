package com.csce331.managerbackend;

/**
 * Interface for items that can be restocked.
 *
 * @author Kedar Panchal
 */
public interface Restockable {
    /**
     * Returns the name of the item.
     *
     * @return the name of the item
     */
    public String getName();

    /**
     * Returns the current quantity on hand.
     *
     * @return the current quantity
     */
    public int getQuantity();

    /**
     * Returns the restock threshold for the item.
     *
     * @return the restock threshold
     */
    public int getRestockThreshold();

    /**
     * Restocks the item by the specified quantity.
     *
     * @param quantity the quantity to restock
     * @return a SQL query to update
     */
    public String restock(int quantity);

    /**
     * Sets the quantity at or below which the item should be restocked.
     *
     * @param threshold the new restock threshold
     * @return a SQL query to update
     */
    public String setRestockThreshold(int threshold);

    /**
     * Returns whether the item's quantity has reached its restock threshold.
     *
     * @return {@code true} if the item needs to be restocked, {@code false} otherwise
     */
    public boolean isRestockNeeded();
}
