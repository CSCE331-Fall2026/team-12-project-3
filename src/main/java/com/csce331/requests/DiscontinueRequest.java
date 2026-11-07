package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.MenuItemSize;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record DiscontinueRequest(String name, MenuItemSize size) {
    public DiscontinueRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (size == null) {
            throw new IllegalArgumentException("Size cannot be null");
        }
    }
}
