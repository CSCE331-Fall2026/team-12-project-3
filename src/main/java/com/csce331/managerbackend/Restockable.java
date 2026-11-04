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
     * Returns the restock threshold for the item.
     *
     * @return the restock threshold
     */
    public int getRestockThreshold();
 }
