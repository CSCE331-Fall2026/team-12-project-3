package com.csce331.managerbackend;

/**
 * A single menu item available to customers, tracking its name, price, size, and category. 
 * Instances are created through the {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class MenuItem {
    /** The name of the menu item. */
    private final String name;
    /** The price of the menu item. */
    private double price;
    /** The size of the menu item. */
    private final MenuItemSize size;
    /** The category of the menu item. */
    private final MenuItemCategory category;

    /**
     * Creates a menu item from the given builder.
     *
     * @param builder the builder holding the menu item's configuration
     */
    private MenuItem(Builder builder) {
        this.name = builder.name;
        this.price = builder.price;
        this.size = builder.size;
        this.category = builder.category;
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
     * Returns the price of the menu item.
     *
     * @return the menu item price
     */
    public double getPrice() {
        return this.price;
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
     * Returns the category of the menu item.
     *
     * @return the menu item category
     */
    public MenuItemCategory getCategory() {
        return this.category;
    }
    
    /**
     * Sets the price of the menu item.
     *
     * @param price the new price
     * @return the SQL query to update the price in the database
     */
    public String setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Menu item price cannot be negative");
        }
        this.price = price;
        return "UPDATE menu_items SET price = " + this.price + " " +
            "WHERE name = '" + this.name + "' AND item_size = '" + this.size + "';";
    }
    
    /**
     * Builder class for constructing {@link MenuItem} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        /** The name of the menu item being built. */
        private final String name;
        /** The price of the menu item being built. */
        private double price;
        /** The size of the menu item being built. */
        private MenuItemSize size;
        /** The category of the menu item being built. */
        private final MenuItemCategory category;
    
        /**
         * Creates a builder for a menu item with the given name and category.
         * The price defaults to 0.0 and the size defaults to {@link MenuItemSize#MEDIUM}.
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
         * Builds the configured {@link MenuItem}.
         *
         * @return a new menu item
         * @throws IllegalArgumentException if the name is null or empty, the price is negative, 
         *         or the size is null
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

            return new MenuItem(this);
        }
    }
}
