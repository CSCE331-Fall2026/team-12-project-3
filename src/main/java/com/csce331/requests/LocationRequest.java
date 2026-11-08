package com.csce331.requests;

import com.csce331.models.InventoryLocation;

/**
 * Request body naming an inventory location.
 *
 * @param location the inventory location
 */
public record LocationRequest(InventoryLocation location) {
    /**
     * Creates the request, rejecting a missing location.
     *
     * @param location the inventory location
     * @throws IllegalArgumentException if the location is null
     */
    public LocationRequest {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }
}
