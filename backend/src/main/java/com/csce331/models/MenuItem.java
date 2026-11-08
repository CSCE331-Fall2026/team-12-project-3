package com.csce331.models;

/**
 * A menu item offered for sale, with its database identifier, name, category,
 * portion size, price, image, and availability. Menu items are created through
 * the {@link Builder} when adding an item to the menu, and with the full
 * constructor when loading one from the database.
 *
 * <p>MenuItem does not override {@code equals} or {@code hashCode}, so two
 * separately loaded copies of the same item are different objects. Compare
 * {@link #getId() ids} when you need to tell whether two items are the same.
 *
 * @author Kedar Panchal
 */
public class MenuItem {
    private final int id;
    private final String name;
    private final MenuItemCategory category;
    private final MenuItemSize size;
    private final double price;
    private final String imageURL;
    private final boolean available;

    /**
     * Creates a menu item with all of its stored details.
     *
     * @param id the item's database identifier, or zero if it has not been
     *           saved
     * @param name the display name
     * @param category the category the item belongs to
     * @param size the portion size
     * @param price the price
     * @param imageURL the image URL or path, or {@code null} if the item has no
     *                 image
     * @param available whether the item can currently be ordered
     */
    public MenuItem(int id, String name, MenuItemCategory category, MenuItemSize size,
            double price, String imageURL, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.size = size;
        this.price = price;
        this.imageURL = imageURL;
        this.available = available;
    }

    /**
     * Returns the item's database identifier.
     *
     * @return the item ID, or zero if the item has not been saved
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the name of the menu item.
     *
     * @return the menu item name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the category of the menu item.
     *
     * @return the menu item category
     */
    public MenuItemCategory getCategory() {
        return this.category;
    }

    /**
     * Returns the size of the menu item.
     *
     * @return the menu item size
     */
    public MenuItemSize getSize() {
        return this.size;
    }

    /**
     * Returns the price of the menu item.
     *
     * @return the menu item price
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Returns the image URL.
     *
     * @return the image URL, or {@code null} if the item has no image
     */
    public String getImageURL() {
        return this.imageURL;
    }

    /**
     * Returns whether the item can currently be ordered.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * Builder class for constructing unsaved {@link MenuItem} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        private final String name;
        private double price;
        private MenuItemSize size;
        private final MenuItemCategory category;

        /**
         * Creates a builder for a menu item with the given name and category.
         * The price defaults to 0.0 and the size defaults to
         * {@link MenuItemSize#MEDIUM}.
         *
         * @param name the name of the menu item
         * @param category the category of the menu item
         */
        public Builder(String name, MenuItemCategory category) {
            this.name = name;
            this.price = 0.0;
            this.size = MenuItemSize.MEDIUM;
            this.category = category;
        }

        /**
         * Sets the price of the menu item.
         *
         * @param price the price of the menu item
         * @return this builder
         */
        public Builder price(double price) {
            this.price = price;
            return this;
        }

        /**
         * Sets the size of the menu item.
         *
         * @param size the size of the menu item
         * @return this builder
         */
        public Builder size(MenuItemSize size) {
            this.size = size;
            return this;
        }

        /**
         * Builds the configured {@link MenuItem}. The item has no database
         * identity yet, no image, and is marked available.
         *
         * @return a new menu item
         * @throws IllegalArgumentException if the name is null or empty, the
         *         price is negative, or the size is null
         */
        public MenuItem build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Menu item name cannot be null or empty");
            }

            if (price < 0) {
                throw new IllegalArgumentException("Menu item price cannot be negative");
            }

            if (size == null) {
                throw new IllegalArgumentException("Menu item size cannot be null");
            }

            return new MenuItem(0, name, category, size, price, null, true);
        }
    }
}
