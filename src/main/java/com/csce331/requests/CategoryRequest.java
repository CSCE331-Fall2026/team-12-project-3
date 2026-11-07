package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.MenuItemCategory;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record CategoryRequest(MenuItemCategory category) {
    public CategoryRequest {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
    }
}
