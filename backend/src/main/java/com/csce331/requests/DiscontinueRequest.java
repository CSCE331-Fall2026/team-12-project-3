package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.MenuItemSize;

/**
 * Request body identifying a menu item to discontinue.
 *
 * @param name the name of the menu item
 * @param size the portion size of the menu item
 */
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record DiscontinueRequest(String name, MenuItemSize size) {
    /**
     * Creates the request, rejecting a missing name or size.
     *
     * @param name the name of the menu item
     * @param size the portion size of the menu item
     * @throws IllegalArgumentException if the name is null or empty, or the size
     *         is null
     */
    public DiscontinueRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (size == null) {
            throw new IllegalArgumentException("Size cannot be null");
        }
    }
}
