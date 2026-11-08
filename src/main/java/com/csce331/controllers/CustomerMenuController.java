package com.csce331.controllers;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;

import com.csce331.models.MenuItem;
import com.csce331.customerbackend.OrderManager;

public class CustomerMenuController {
    private static final OrderManager orderManager = new OrderManager();

    public static void popularItems(Context ctx) {
        try {
            Map<MenuItem, Boolean> popularItems = orderManager.getPopularItems();
            ctx.status(200).json(popularItems);
        } catch (Exception e) {
            ctx.status(500).result("Error retrieving popular items: " + e.getMessage());
        }
    }
}
