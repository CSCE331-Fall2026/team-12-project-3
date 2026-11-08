package com.csce331.requests;

import com.csce331.models.InventoryLocation;

public record LocationRequest(InventoryLocation location) {
    public LocationRequest {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }
}
