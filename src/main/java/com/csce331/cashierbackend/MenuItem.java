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
     * Sets the database ID of the menu item.
     *
     * @param id the unique item ID to set
     */
    public void setId(int id) {
        this.id = id;
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
     * Sets the display name of the menu item.
     *
     * @param name the item name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the category of the menu item.
     *
     * @param itemCategory the item category to set
     */
    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
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
     * Sets the size of the menu item.
     *
     * @param itemSize the item size to set
     */
    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
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
     * Sets the price of the menu item.
     *
     * @param price the item price to set
     */
    public void setPrice(double price) {
        this.price = price;
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
     * Sets the image URL of the menu item.
     *
     * @param imageURL the image URL string to set
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Checks if the menu item is available for purchase.
     *
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status of the menu item.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
