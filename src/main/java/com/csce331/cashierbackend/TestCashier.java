package com.csce331.cashierbackend;

import java.util.Map;

/**
 * Driver/test class to verify cashier backend operations, menu item queries,
 * cart item additions, and order execution against the database.
 */
public class TestCashier {

    /** Default sample price for testing order submission. */
    private static final double SAMPLE_ORDER_PRICE = 15.0;

    /**
     * Private constructor to prevent instantiation.
     */
    private TestCashier() {
    }

    /**
     * Executes cashier backend testing routines.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        OrderManager manager = new OrderManager();

        System.out.println("--- Popular Items ---");
        Map<MenuItem, Boolean> popular = manager.getPopularItems();
        for (MenuItem item : popular.keySet()) {
            System.out.println(item.getName() + " | Available: " + popular.get(item));
        }

        if (!popular.isEmpty()) {
            MenuItem sampleItem = popular.keySet().iterator().next();
            manager.addItem(sampleItem);
            manager.addItem(sampleItem);
            System.out.println("\nAdded " + sampleItem.getName() + " to order.");

            manager.order(PaymentMethod.CREDIT_CARD, SAMPLE_ORDER_PRICE);
            System.out.println("Order successfully placed!");
        }
    }
}