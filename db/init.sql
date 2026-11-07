-- CUSTOM DATA TYPES ----------------------------------------------------------
CREATE TYPE payment_type AS ENUM (
    'cash', 
    'credit card', 
    'debit card', 
    'dining dollars', 
    'maroon meals'
);
CREATE TYPE menu_category AS ENUM (
    'side', 
    'entree',
    'drink', 
    'appetizer'
);
CREATE TYPE size_category AS ENUM ('small', 'medium', 'large');
CREATE TYPE inventory_location AS ENUM ('pantry', 'refrigerator', 'freezer');
CREATE TYPE ingredient_unit AS ENUM ('kilograms', 'liters', 'items');

-- RESET DATABASE -------------------------------------------------------------
DROP TABLE IF EXISTS order_menu;
DROP TABLE IF EXISTS menu_ingredients;
DROP TABLE IF EXISTS order_utensils;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS utensils;

-- DATABASE TABLES ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    order_time TIME NOT NULL,
    order_date DATE NOT NULL,
    payment_method payment_type DEFAULT 'credit card',
    price DECIMAL(5, 2) NOT NULL CHECK (price >= 0),
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS menu_items (
    name TEXT NOT NULL,
    item_category menu_category NOT NULL,
    item_size size_category NOT NULL,
    price DECIMAL(4, 2) NOT NULL CHECK (price >= 0),
    image BYTEA,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS ingredients (
    name TEXT NOT NULL UNIQUE,
    shelf_life INT NOT NULL CHECK (shelf_life >= 0) DEFAULT 0,
    restock_threshold INT NOT NULL CHECK (restock_threshold >= 0) DEFAULT 0,
    inventory_type inventory_location NOT NULL,
    unit ingredient_unit NOT NULL,
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS inventory (
    ingredient_id INT REFERENCES ingredients (id) NOT NULL,
    quantity DECIMAL(10, 4) NOT NULL CHECK (quantity >= 0) DEFAULT 0,
    restock_date DATE NOT NULL,
    id SERIAL PRIMARY KEY,

    CONSTRAINT unique_ingredient_restock UNIQUE (ingredient_id, restock_date)
);

CREATE TABLE IF NOT EXISTS utensils (
    name TEXT NOT NULL,
    quantity INT NOT NULL CHECK (current_amount >= 0) DEFAULT 0,
    restock_threshold INT NOT NULL CHECK (restock_threshold >= 0) DEFAULT 0,
    id SERIAL PRIMARY KEY
);

-- JUNCTION TABLES ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_menu (
    order_id INT REFERENCES orders (id) NOT NULL,
    menu_item_id INT REFERENCES menu_items (id) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS menu_ingredients (
    menu_item_id INT REFERENCES menu_items (id) NOT NULL,
    ingredient_id INT REFERENCES ingredients (id) NOT NULL,
    quantity DECIMAL(10, 4) NOT NULL CHECK (quantity >= 0) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_utensils (
    order_id INT REFERENCES orders (id) NOT NULL,
    utensil_id INT REFERENCES utensils (id) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0) DEFAULT 0
);

-- MENU POPULATION ------------------------------------------------------------
-- Add entree items
INSERT INTO menu_items (name, item_category, item_size, price) VALUES
    ('Mushroom Chicken', 'entree', 'small', 5.40),
    ('Mushroom Chicken', 'entree', 'medium', 8.70),
    ('Mushroom Chicken', 'entree', 'large', 12.15),
    ('Kung Pao Chicken', 'entree', 'small', 5.40),
    ('Kung Pao Chicken', 'entree', 'medium', 8.70),
    ('Kung Pao Chicken', 'entree', 'large', 12.15),
    ('The Original Orange Chicken', 'entree', 'small', 5.40),
    ('The Original Orange Chicken', 'entree', 'medium', 8.70),
    ('The Original Orange Chicken', 'entree', 'large', 12.15),
    ('Honey Sesame Chicken Breast', 'entree', 'small', 5.40),
    ('Honey Sesame Chicken Breast', 'entree', 'medium', 8.70),
    ('Honey Sesame Chicken Breast', 'entree', 'large', 12.15),
    ('Grilled Teriyaki Chicken', 'entree', 'small', 5.40),
    ('Grilled Teriyaki Chicken', 'entree', 'medium', 8.70),
    ('Grilled Teriyaki Chicken', 'entree', 'large', 12.15),
    ('Broccoli Beef', 'entree', 'small', 5.40),
    ('Broccoli Beef', 'entree', 'medium', 8.70),
    ('Broccoli Beef', 'entree', 'large', 12.15),
    ('Beijing Beef', 'entree', 'small', 5.40),
    ('Beijing Beef', 'entree', 'medium', 8.70),
    ('Beijing Beef', 'entree', 'large', 12.15);

-- Add side items
INSERT INTO menu_items (name, item_category, item_size, price) VALUES
    ('White Steamed Rice', 'side', 'medium', 4.60),
    ('White Steamed Rice', 'side', 'large', 6.05),
    ('Fried Rice', 'side', 'medium', 4.60),
    ('Fried Rice', 'side', 'large', 6.05),
    ('Chow Mein', 'side', 'medium', 4.60),
    ('Chow Mein', 'side', 'large', 6.05),
    ('Super Greens', 'side', 'medium', 4.60),
    ('Super Greens', 'side', 'large', 6.05);

-- Add drink items
INSERT INTO menu_items (name, item_category, item_size, price) VALUES
    ('Coca Cola', 'drink', 'medium', 2.60),
    ('Coca Cola', 'drink', 'large', 3.00),
    ('Dr. Pepper', 'drink', 'medium', 2.60),
    ('Dr. Pepper', 'drink', 'large', 3.00),
    ('Sprite', 'drink', 'medium', 2.60),
    ('Sprite', 'drink', 'large', 3.00),
    ('Fanta Orange', 'drink', 'medium', 2.60),
    ('Fanta Orange', 'drink', 'large', 3.00),
    ('Dasani', 'drink', 'small', 1.80),
    ('Dasani', 'drink', 'large', 3.00),
    ('Dasani', 'drink', 'medium', 2.60);

-- Add appetizer items
INSERT INTO menu_items (name, item_category, item_size, price) VALUES
    ('Veggie Spring Roll', 'appetizer', 'small', 2.10),
    ('Veggie Spring Roll', 'appetizer', 'large', 11.70),
    ('Chicken Egg Roll', 'appetizer', 'small', 2.10),
    ('Chicken Egg Roll', 'appetizer', 'large', 11.70),
    ('Cream Cheese Rangoon', 'appetizer', 'small', 2.10),
    ('Cream Cheese Rangoon', 'appetizer', 'large', 8.00),
    ('Fortune Cookie', 'appetizer', 'medium', 0.00);

-- INGREDIENT POPULATION ------------------------------------------------------
-- Add entree ingredients
INSERT INTO ingredients (name, shelf_life, restock_threshold, inventory_type, unit) VALUES
    ('Boneless Chicken Breast', 270, 160, 'freezer', 'kilograms'),
    ('Beef Strips', 120, 40, 'freezer', 'kilograms'),
    ('Breaded Orange Chicken Coating', 180, 20, 'pantry', 'kilograms'),
    ('Cornstarch', 360, 10, 'pantry', 'kilograms'),
    ('All-Purpose Flour', 180, 15, 'pantry', 'kilograms'),
    ('Frying Oil', 360, 200, 'pantry', 'liters'),
    ('Sesame Oil', 360, 10, 'pantry', 'liters'),
    ('Soy Sauce', 180, 40, 'pantry', 'liters'),
    ('Oyster Sauce', 30, 25, 'refrigerator', 'liters'),
    ('Teriyaki Sauce', 30, 30, 'refrigerator', 'liters'),
    ('Orange Sauce', 30, 40, 'refrigerator', 'liters'),
    ('Sweet and Sour Sauce', 30, 20, 'refrigerator', 'liters'),
    ('Honey', 720, 20, 'pantry', 'kilograms'),
    ('Granulated Sugar', 720, 30, 'pantry', 'kilograms'),
    ('Rice Vinegar', 360, 15, 'pantry', 'liters'),
    ('White Vinegar', 360, 10, 'pantry', 'liters'),
    ('Garlic', 14, 20, 'refrigerator', 'kilograms'),
    ('Ginger', 7, 10, 'refrigerator', 'kilograms'),
    ('Yellow Onions', 10, 40, 'refrigerator', 'kilograms'),
    ('Green Onions', 7, 15, 'refrigerator', 'kilograms'),
    ('Bell Peppers', 7, 45, 'refrigerator', 'kilograms'),
    ('Zucchini', 4, 25, 'refrigerator', 'kilograms'),
    ('Mushrooms', 5, 35, 'refrigerator', 'kilograms'),
    ('Broccoli', 3, 45, 'refrigerator', 'kilograms'),
    ('Celery', 5, 15, 'refrigerator', 'kilograms'),
    ('Water Chestnuts', 3, 15, 'refrigerator', 'kilograms'),
    ('Bamboo Shoots', 3, 15, 'refrigerator', 'kilograms'),
    ('Red Chili Pepper Flakes', 360, 5, 'pantry', 'kilograms'),
    ('Dried Chilis', 720, 5, 'pantry', 'kilograms'),
    ('Peanuts', 180, 10, 'pantry', 'kilograms'),
    ('Sesame Seeds', 180, 5, 'pantry', 'kilograms'),
    ('Black Pepper', 360, 5, 'pantry', 'kilograms'),
    ('Salt', 3600, 15, 'pantry', 'kilograms'),
    ('Chicken Broth', 5, 25, 'refrigerator', 'liters');

-- Add side ingredients
INSERT INTO ingredients (name, shelf_life, restock_threshold, inventory_type, unit) VALUES
    ('White Rice', 180, 135, 'pantry', 'kilograms'),
    ('Chow Mein Noodles', 360, 100, 'pantry', 'kilograms'),
    ('Kale', 3, 25, 'refrigerator', 'kilograms'),
    ('Cabbage', 5, 40, 'refrigerator', 'kilograms'),
    ('Carrots', 7, 30, 'refrigerator', 'kilograms');

-- Add drink ingredients
INSERT INTO ingredients (name, shelf_life, restock_threshold, inventory_type, unit) VALUES
    ('Coca Cola Syrup', 180, 160, 'pantry', 'liters'),
    ('Dr. Pepper Syrup', 180, 110, 'pantry', 'liters'),
    ('Sprite Syrup', 180, 100, 'pantry', 'liters'),
    ('Fanta Orange Syrup', 180, 60, 'pantry', 'liters'),
    ('Dasani Water', 360, 140, 'pantry', 'items'),
    ('Ice', 1, 25, 'freezer', 'kilograms');

-- Add appetizer ingredients
INSERT INTO ingredients (name, shelf_life, restock_threshold, inventory_type, unit) VALUES
    ('Shrimp', 180, 10, 'freezer', 'kilograms'),
    ('Egg Roll Filling', 180, 20, 'refrigerator', 'kilograms'),
    ('Eggs', 21, 200, 'refrigerator', 'kilograms'),
    ('Cream Cheese', 7, 25, 'refrigerator', 'kilograms'),
    ('Wonton Wrappers', 7, 4100, 'refrigerator', 'items'),
    ('Egg Roll Wrappers', 7, 1800, 'refrigerator', 'items'),
    ('Spring Roll Wrappers', 180, 1800, 'freezer', 'items'),
    ('Fortune Cookies', 180, 900, 'pantry', 'items');

-- UTENSIL POPULATION ---------------------------------------------------------
-- Add containers
INSERT INTO utensils (name, current_amount, restock_threshold) VALUES
    ('Takeout Carton', 0, 600),
    ('Clamshell Container', 0, 1000),
    ('Portion Cup', 0, 750),
    ('Medium Cup', 0, 1500),
    ('Large Cup', 0, 750),
    ('Paper Bag', 0, 1000);

-- Add utensils
INSERT INTO utensils (name, current_amount, restock_threshold) VALUES
    ('Fork', 0, 1000),
    ('Knife', 0, 1000),
    ('Spoon', 0, 1000),
    ('Chopsticks', 0, 1000);

-- Add miscellaneous
INSERT INTO utensils (name, current_amount, restock_threshold) VALUES
    ('Napkin', 0, 2000),
    ('Straw', 0, 1500);

-- JUNCTION TABLE POPULATION --------------------------------------------------
-- Populate the menu_ingredients junction table. Each row links a menu item
-- (matched by name and size) to one of its ingredients, with the quantity of
-- that ingredient used per serving in the ingredient's own unit (kilograms,
-- liters, or items). Quantities scale by portion size (small/medium/large).

-- Mushroom Chicken (chicken, mushrooms, zucchini, soy sauce, garlic, ginger, sesame oil, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Boneless Chicken Breast', 0.1400),
    ('medium', 'Boneless Chicken Breast', 0.2800),
    ('large',  'Boneless Chicken Breast', 0.4200),
    ('small',  'Mushrooms', 0.0600),
    ('medium', 'Mushrooms', 0.1200),
    ('large',  'Mushrooms', 0.1800),
    ('small',  'Zucchini', 0.0400),
    ('medium', 'Zucchini', 0.0800),
    ('large',  'Zucchini', 0.1200),
    ('small',  'Soy Sauce', 0.0150),
    ('medium', 'Soy Sauce', 0.0300),
    ('large',  'Soy Sauce', 0.0450),
    ('small',  'Garlic', 0.0050),
    ('medium', 'Garlic', 0.0100),
    ('large',  'Garlic', 0.0150),
    ('small',  'Ginger', 0.0030),
    ('medium', 'Ginger', 0.0060),
    ('large',  'Ginger', 0.0090),
    ('small',  'Sesame Oil', 0.0050),
    ('medium', 'Sesame Oil', 0.0100),
    ('large',  'Sesame Oil', 0.0150),
    ('small',  'Frying Oil', 0.0200),
    ('medium', 'Frying Oil', 0.0400),
    ('large',  'Frying Oil', 0.0600)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Mushroom Chicken' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Kung Pao Chicken (chicken, peanuts, bell peppers, zucchini, dried chilis, red chili flakes, garlic, ginger, soy sauce, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Boneless Chicken Breast', 0.1400),
    ('medium', 'Boneless Chicken Breast', 0.2800),
    ('large',  'Boneless Chicken Breast', 0.4200),
    ('small',  'Peanuts', 0.0300),
    ('medium', 'Peanuts', 0.0600),
    ('large',  'Peanuts', 0.0900),
    ('small',  'Bell Peppers', 0.0400),
    ('medium', 'Bell Peppers', 0.0800),
    ('large',  'Bell Peppers', 0.1200),
    ('small',  'Zucchini', 0.0400),
    ('medium', 'Zucchini', 0.0800),
    ('large',  'Zucchini', 0.1200),
    ('small',  'Dried Chilis', 0.0050),
    ('medium', 'Dried Chilis', 0.0100),
    ('large',  'Dried Chilis', 0.0150),
    ('small',  'Red Chili Pepper Flakes', 0.0020),
    ('medium', 'Red Chili Pepper Flakes', 0.0040),
    ('large',  'Red Chili Pepper Flakes', 0.0060),
    ('small',  'Garlic', 0.0050),
    ('medium', 'Garlic', 0.0100),
    ('large',  'Garlic', 0.0150),
    ('small',  'Ginger', 0.0030),
    ('medium', 'Ginger', 0.0060),
    ('large',  'Ginger', 0.0090),
    ('small',  'Soy Sauce', 0.0150),
    ('medium', 'Soy Sauce', 0.0300),
    ('large',  'Soy Sauce', 0.0450),
    ('small',  'Frying Oil', 0.0200),
    ('medium', 'Frying Oil', 0.0400),
    ('large',  'Frying Oil', 0.0600)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Kung Pao Chicken' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- The Original Orange Chicken (chicken, orange chicken coating, cornstarch, frying oil, orange sauce)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Boneless Chicken Breast', 0.1400),
    ('medium', 'Boneless Chicken Breast', 0.2800),
    ('large',  'Boneless Chicken Breast', 0.4200),
    ('small',  'Breaded Orange Chicken Coating', 0.0400),
    ('medium', 'Breaded Orange Chicken Coating', 0.0800),
    ('large',  'Breaded Orange Chicken Coating', 0.1200),
    ('small',  'Cornstarch', 0.0150),
    ('medium', 'Cornstarch', 0.0300),
    ('large',  'Cornstarch', 0.0450),
    ('small',  'Frying Oil', 0.0300),
    ('medium', 'Frying Oil', 0.0600),
    ('large',  'Frying Oil', 0.0900),
    ('small',  'Orange Sauce', 0.0300),
    ('medium', 'Orange Sauce', 0.0600),
    ('large',  'Orange Sauce', 0.0900)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'The Original Orange Chicken' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Honey Sesame Chicken Breast (chicken, flour, cornstarch, frying oil, honey, sesame seeds, bell peppers, sesame oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Boneless Chicken Breast', 0.1400),
    ('medium', 'Boneless Chicken Breast', 0.2800),
    ('large',  'Boneless Chicken Breast', 0.4200),
    ('small',  'All-Purpose Flour', 0.0300),
    ('medium', 'All-Purpose Flour', 0.0600),
    ('large',  'All-Purpose Flour', 0.0900),
    ('small',  'Cornstarch', 0.0150),
    ('medium', 'Cornstarch', 0.0300),
    ('large',  'Cornstarch', 0.0450),
    ('small',  'Frying Oil', 0.0300),
    ('medium', 'Frying Oil', 0.0600),
    ('large',  'Frying Oil', 0.0900),
    ('small',  'Honey', 0.0250),
    ('medium', 'Honey', 0.0500),
    ('large',  'Honey', 0.0750),
    ('small',  'Sesame Seeds', 0.0050),
    ('medium', 'Sesame Seeds', 0.0100),
    ('large',  'Sesame Seeds', 0.0150),
    ('small',  'Bell Peppers', 0.0400),
    ('medium', 'Bell Peppers', 0.0800),
    ('large',  'Bell Peppers', 0.1200),
    ('small',  'Sesame Oil', 0.0050),
    ('medium', 'Sesame Oil', 0.0100),
    ('large',  'Sesame Oil', 0.0150)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Honey Sesame Chicken Breast' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Grilled Teriyaki Chicken (chicken, teriyaki sauce, sesame seeds, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Boneless Chicken Breast', 0.1600),
    ('medium', 'Boneless Chicken Breast', 0.3200),
    ('large',  'Boneless Chicken Breast', 0.4800),
    ('small',  'Teriyaki Sauce', 0.0300),
    ('medium', 'Teriyaki Sauce', 0.0600),
    ('large',  'Teriyaki Sauce', 0.0900),
    ('small',  'Sesame Seeds', 0.0030),
    ('medium', 'Sesame Seeds', 0.0060),
    ('large',  'Sesame Seeds', 0.0090),
    ('small',  'Frying Oil', 0.0100),
    ('medium', 'Frying Oil', 0.0200),
    ('large',  'Frying Oil', 0.0300)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Grilled Teriyaki Chicken' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Broccoli Beef (beef strips, broccoli, soy sauce, oyster sauce, garlic, ginger, cornstarch, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Beef Strips', 0.1200),
    ('medium', 'Beef Strips', 0.2400),
    ('large',  'Beef Strips', 0.3600),
    ('small',  'Broccoli', 0.1000),
    ('medium', 'Broccoli', 0.2000),
    ('large',  'Broccoli', 0.3000),
    ('small',  'Soy Sauce', 0.0150),
    ('medium', 'Soy Sauce', 0.0300),
    ('large',  'Soy Sauce', 0.0450),
    ('small',  'Oyster Sauce', 0.0150),
    ('medium', 'Oyster Sauce', 0.0300),
    ('large',  'Oyster Sauce', 0.0450),
    ('small',  'Garlic', 0.0050),
    ('medium', 'Garlic', 0.0100),
    ('large',  'Garlic', 0.0150),
    ('small',  'Ginger', 0.0030),
    ('medium', 'Ginger', 0.0060),
    ('large',  'Ginger', 0.0090),
    ('small',  'Cornstarch', 0.0100),
    ('medium', 'Cornstarch', 0.0200),
    ('large',  'Cornstarch', 0.0300),
    ('small',  'Frying Oil', 0.0150),
    ('medium', 'Frying Oil', 0.0300),
    ('large',  'Frying Oil', 0.0450)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Broccoli Beef' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Beijing Beef (beef strips, bell peppers, yellow onions, cornstarch, frying oil, sweet and sour sauce, granulated sugar, rice vinegar)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Beef Strips', 0.1200),
    ('medium', 'Beef Strips', 0.2400),
    ('large',  'Beef Strips', 0.3600),
    ('small',  'Bell Peppers', 0.0500),
    ('medium', 'Bell Peppers', 0.1000),
    ('large',  'Bell Peppers', 0.1500),
    ('small',  'Yellow Onions', 0.0400),
    ('medium', 'Yellow Onions', 0.0800),
    ('large',  'Yellow Onions', 0.1200),
    ('small',  'Cornstarch', 0.0150),
    ('medium', 'Cornstarch', 0.0300),
    ('large',  'Cornstarch', 0.0450),
    ('small',  'Frying Oil', 0.0300),
    ('medium', 'Frying Oil', 0.0600),
    ('large',  'Frying Oil', 0.0900),
    ('small',  'Sweet and Sour Sauce', 0.0300),
    ('medium', 'Sweet and Sour Sauce', 0.0600),
    ('large',  'Sweet and Sour Sauce', 0.0900),
    ('small',  'Granulated Sugar', 0.0100),
    ('medium', 'Granulated Sugar', 0.0200),
    ('large',  'Granulated Sugar', 0.0300),
    ('small',  'Rice Vinegar', 0.0100),
    ('medium', 'Rice Vinegar', 0.0200),
    ('large',  'Rice Vinegar', 0.0300)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Beijing Beef' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- White Steamed Rice (white rice, salt)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'White Rice', 0.2500),
    ('large',  'White Rice', 0.3750),
    ('medium', 'Salt', 0.0020),
    ('large',  'Salt', 0.0030)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'White Steamed Rice' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Fried Rice (white rice, eggs, green onions, carrots, soy sauce, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'White Rice', 0.2500),
    ('large',  'White Rice', 0.3750),
    ('medium', 'Eggs', 0.0400),
    ('large',  'Eggs', 0.0600),
    ('medium', 'Green Onions', 0.0200),
    ('large',  'Green Onions', 0.0300),
    ('medium', 'Carrots', 0.0300),
    ('large',  'Carrots', 0.0450),
    ('medium', 'Soy Sauce', 0.0200),
    ('large',  'Soy Sauce', 0.0300),
    ('medium', 'Frying Oil', 0.0150),
    ('large',  'Frying Oil', 0.0225)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Fried Rice' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Chow Mein (chow mein noodles, cabbage, celery, yellow onions, soy sauce, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Chow Mein Noodles', 0.2200),
    ('large',  'Chow Mein Noodles', 0.3300),
    ('medium', 'Cabbage', 0.0500),
    ('large',  'Cabbage', 0.0750),
    ('medium', 'Celery', 0.0300),
    ('large',  'Celery', 0.0450),
    ('medium', 'Yellow Onions', 0.0300),
    ('large',  'Yellow Onions', 0.0450),
    ('medium', 'Soy Sauce', 0.0200),
    ('large',  'Soy Sauce', 0.0300),
    ('medium', 'Frying Oil', 0.0150),
    ('large',  'Frying Oil', 0.0225)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Chow Mein' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Super Greens (broccoli, kale, cabbage, garlic, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Broccoli', 0.1200),
    ('large',  'Broccoli', 0.1800),
    ('medium', 'Kale', 0.0800),
    ('large',  'Kale', 0.1200),
    ('medium', 'Cabbage', 0.0800),
    ('large',  'Cabbage', 0.1200),
    ('medium', 'Garlic', 0.0050),
    ('large',  'Garlic', 0.0075),
    ('medium', 'Frying Oil', 0.0100),
    ('large',  'Frying Oil', 0.0150)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Super Greens' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Coca Cola (coca cola syrup, ice)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Coca Cola Syrup', 0.0800),
    ('large',  'Coca Cola Syrup', 0.1200),
    ('medium', 'Ice', 0.1500),
    ('large',  'Ice', 0.2500)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Coca Cola' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Dr. Pepper (dr. pepper syrup, ice)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Dr. Pepper Syrup', 0.0800),
    ('large',  'Dr. Pepper Syrup', 0.1200),
    ('medium', 'Ice', 0.1500),
    ('large',  'Ice', 0.2500)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Dr. Pepper' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Sprite (sprite syrup, ice)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Sprite Syrup', 0.0800),
    ('large',  'Sprite Syrup', 0.1200),
    ('medium', 'Ice', 0.1500),
    ('large',  'Ice', 0.2500)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Sprite' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Fanta Orange (fanta orange syrup, ice)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Fanta Orange Syrup', 0.0800),
    ('large',  'Fanta Orange Syrup', 0.1200),
    ('medium', 'Ice', 0.1500),
    ('large',  'Ice', 0.2500)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Fanta Orange' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Dasani (bottled water)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small',  'Dasani Water', 1),
    ('medium', 'Dasani Water', 1),
    ('large',  'Dasani Water', 1)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Dasani' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Veggie Spring Roll (spring roll wrappers, cabbage, carrots, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small', 'Spring Roll Wrappers', 2),
    ('large', 'Spring Roll Wrappers', 12),
    ('small', 'Cabbage', 0.0400),
    ('large', 'Cabbage', 0.2400),
    ('small', 'Carrots', 0.0200),
    ('large', 'Carrots', 0.1200),
    ('small', 'Frying Oil', 0.0150),
    ('large', 'Frying Oil', 0.0900)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Veggie Spring Roll' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Chicken Egg Roll (egg roll wrappers, egg roll filling, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small', 'Egg Roll Wrappers', 2),
    ('large', 'Egg Roll Wrappers', 12),
    ('small', 'Egg Roll Filling', 0.0800),
    ('large', 'Egg Roll Filling', 0.4800),
    ('small', 'Frying Oil', 0.0150),
    ('large', 'Frying Oil', 0.0900)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Chicken Egg Roll' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Cream Cheese Rangoon (wonton wrappers, cream cheese, frying oil)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('small', 'Wonton Wrappers', 3),
    ('large', 'Wonton Wrappers', 12),
    ('small', 'Cream Cheese', 0.0450),
    ('large', 'Cream Cheese', 0.1800),
    ('small', 'Frying Oil', 0.0150),
    ('large', 'Frying Oil', 0.0600)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Cream Cheese Rangoon' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;

-- Fortune Cookie (fortune cookies)
INSERT INTO menu_ingredients (menu_item_id, ingredient_id, quantity)
SELECT m.id, i.id, v.quantity
FROM (VALUES
    ('medium', 'Fortune Cookies', 1)
) AS v(size, ingredient, quantity)
JOIN menu_items m ON m.name = 'Fortune Cookie' AND m.item_size::text = v.size
JOIN ingredients i ON i.name = v.ingredient;
