\pset pager off
\echo Weekly Sales Report - 52 weeks of sales history
\echo 
SELECT 
        DATE_TRUNC('week', order_date)::DATE AS week_start, 
        SUM(price) AS weekly_sales
FROM orders
GROUP BY week_start
ORDER BY week_start
LIMIT 52;

\echo Invalid Order Dates - Checking if orders fall within operation hours
\echo 

SELECT 
        order_date AS invalid_order_date,
        order_time AS invalid_order_time,
        id AS invalid_order_id 
FROM orders 
WHERE (((EXTRACT(DOW FROM order_date) BETWEEN 1 AND 5) AND order_time NOT BETWEEN '09:00:00' AND '21:00:00') OR
       ((EXTRACT(DOW FROM order_date) = 0) AND order_time NOT BETWEEN '10:00:00' AND '15:00:00') OR
       ((EXTRACT(DOW FROM order_date) = 6) AND order_time NOT BETWEEN '12:00:00' AND '19:00:00'))
ORDER BY order_date, order_time;

\echo Hourly Report - order count by hour
\echo 
--sum up prices and count number of orders by hour
SELECT 
        EXTRACT(HOUR FROM order_time) AS order_hour, 
        COUNT(*) as order_count, 
        SUM(price) as revenue
FROM orders
GROUP BY order_hour
ORDER BY order_hour;


\echo Best Day - identify the highest sales day
\echo 

SELECT
        TO_CHAR(order_date, 'FMMonth FMDD') AS day, 
        SUM(Price) AS daily_sales
FROM orders
GROUP BY order_date
ORDER BY daily_sales DESC
LIMIT 10;

\echo Worst Day - identify the lowest sales day
\echo 
SELECT
        TO_CHAR(order_date, 'FMMonth FMDD') AS day,
        SUM(price) AS daily_sales
FROM orders
GROUP BY order_date
ORDER BY daily_sales ASC
LIMIT 3;

\echo Inventory items for 20 menu items: select count of inventory items from inventory and menu grouped by menu item
\echo 
-- e.g. "chicken fingers uses 12 items"

SELECT menu_items.name AS menu_item, 
        COUNT(ingredients.id) AS ingredient_types,
        SUM(menu_ingredients.quantity) AS ingredient_units_used
FROM menu_items
INNER JOIN menu_ingredients ON menu_items.id = menu_ingredients.menu_item_id
INNER JOIN ingredients ON menu_ingredients.ingredient_id = ingredients.id
GROUP BY menu_item
ORDER BY ingredient_units_used DESC
LIMIT 20;

\echo Best of the Worst - Select the bottom 1 sum (slowest day) and report on the best selling item
\echo 
WITH slowest_day AS (
        SELECT 
                order_date, 
                SUM(price) AS total_sales
        FROM orders
        GROUP BY order_date
        ORDER BY total_sales ASC
        LIMIT 1
)
SELECT          
        TO_CHAR(slowest_day.order_date, 'FMMonth FMDD') as day,
        menu_items.name AS menu_item,
        SUM(order_menu.quantity) AS menu_items_sold
FROM slowest_day
JOIN orders ON orders.order_date = slowest_day.order_date
INNER JOIN order_menu ON orders.id = order_menu.order_id
INNER JOIN menu_items ON order_menu.menu_item_id = menu_items.id
GROUP BY slowest_day.order_date, menu_items.name
ORDER BY menu_items_sold DESC
LIMIT 1; 


\echo Restock Report - ingredients with least inventory relative to threshold 
\echo
SELECT 
        ingredients.name AS ingredient,
        inventory.quantity as current_amount,
        ingredients.restock_threshold,
        (inventory.quantity - restock_threshold) AS surplus,
        inventory.quantity::FLOAT / NULLIF(restock_threshold, 0) AS stock_ratio
FROM ingredients
JOIN inventory ON inventory.ingredient_id = ingredients.id
ORDER BY surplus ASC;


