package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.MenuItemCategory;

/**
 * Request body naming the menu category to list items from.
 *
 * @param category the category to filter by
 */
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record CategoryRequest(MenuItemCategory category) {
    /**
     * Creates the request, rejecting a missing category.
     *
     * @param category the category to filter by
     * @throws IllegalArgumentException if the category is null
     */
    public CategoryRequest {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
    }
}
