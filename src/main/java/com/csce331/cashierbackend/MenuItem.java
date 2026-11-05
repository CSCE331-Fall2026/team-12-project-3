package com.csce331.cashierbackend;

/**
 * Represents a menu item available for purchase.
 * Stores core item properties and provides equality based on unique item identifier.
 */
public class MenuItem {

    /** The unique database identifier for the menu item. */
    private int id;

    /** The display name of the menu item. */
    private String name;

    /** The category of the menu item (e.g., entree, side, drink, appetizer). */
    private String itemCategory;

    /** The size category of the menu item (e.g., small, medium, large). */
    private String itemSize;

    /** The price of the menu item. */
    private double price;

    /** The image URL or path associated with the menu item. */
    private String imageURL;

    /** Indicates whether the menu item is currently available for ordering. */
    private boolean available;

    /**
     * Constructs a new MenuItem instance with all properties.
     *
     * @param id           the unique database identifier
     * @param name         the display name of the menu item
     * @param itemCategory the category of the item
     * @param itemSize     the size of the item
     * @param price        the price of the item
     * @param imageURL     the URL string or file path for the item image
     * @param available    true if item can be ordered, false otherwise
     */
    public MenuItem(int id, String name, String itemCategory, String itemSize, double price, String imageURL, boolean available) {
        this.id = id;
        this.name = name;
        this.itemCategory = itemCategory;
        this.itemSize = itemSize;
        this.price = price;
        this.imageURL = imageURL;
        this.available = available;
    }

    /**
     * Retrieves the database ID of the menu item.
     *
     * @return the unique item ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the display name of the menu item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the category of the menu item.
     *
     * @return the item category
     */
    public String getItemCategory() {
        return itemCategory;
    }

    /**
     * Retrieves the size of the menu item.
     *
     * @return the item size
     */
    public String getItemSize() {
        return itemSize;
    }

    /**
     * Retrieves the price of the menu item.
     *
     * @return the item price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retrieves the image URL of the menu item.
     *
     * @return the image URL string
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Checks if the menu item is available for purchase.
     *
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }
}
