package com.csce331.cashierbackend;

/**
 * A menu item offered for sale, with its name, category, size, price, and
 * image.
 *
 * <p>MenuItem does not override {@code equals} or {@code hashCode}, so two
 * separately loaded copies of the same item are different objects. Compare
 * {@link #getId() ids} when you need to tell whether two items are the same.
 */
public class MenuItem {

    private int id;
    private String name;
    private String itemCategory;
    private String itemSize;
    private double price;
    private String imageURL;
    private boolean available;

    /**
     * Creates a menu item.
     *
     * @param id           the item's database identifier
     * @param name         the display name
     * @param itemCategory the category, such as {@code "entree"}
     * @param itemSize     the size, such as {@code "medium"}
     * @param price        the price
     * @param imageURL     the image URL or path, or {@code null} if the item has
     *                     no image
     * @param available    whether the item can currently be ordered
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
     * Returns the item's database identifier.
     *
     * @return the item ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the display name.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the category.
     *
     * @return the item category
     */
    public String getItemCategory() {
        return itemCategory;
    }

    /**
     * Returns the size.
     *
     * @return the item size
     */
    public String getItemSize() {
        return itemSize;
    }

    /**
     * Returns the price.
     *
     * @return the item price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the image URL.
     *
     * @return the image URL, or {@code null} if the item has no image
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Returns whether the item can currently be ordered.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean isAvailable() {
        return available;
    }
}
