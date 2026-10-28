"""Generate sql file that fills inventory table with ingredient stuff"""

import random
from datetime import date, timedelta

OUTPUT_PATH = "db/inventory.sql"
INGREDIENTS = [
    ("Boneless Chicken Breast", 270, 160, "kilograms"),
    ("Beef Strips", 120, 40, "kilograms"),
    ("Breaded Orange Chicken Coating", 180, 20, "kilograms"),
    ("Cornstarch", 360, 10, "kilograms"),
    ("All-Purpose Flour", 180, 15, "kilograms"),
    ("Frying Oil", 360, 200, "liters"),
    ("Sesame Oil", 360, 10, "liters"),
    ("Soy Sauce", 180, 40, "liters"),
    ("Oyster Sauce", 30, 25, "liters"),
    ("Teriyaki Sauce", 30, 30, "liters"),
    ("Orange Sauce", 30, 40, "liters"),
    ("Sweet and Sour Sauce", 30, 20, "liters"),
    ("Honey", 720, 20, "kilograms"),
    ("Granulated Sugar", 720, 30, "kilograms"),
    ("Rice Vinegar", 360, 15, "liters"),
    ("White Vinegar", 360, 10, "liters"),
    ("Garlic", 14, 20, "kilograms"),
    ("Ginger", 7, 10, "kilograms"),
    ("Yellow Onions", 10, 40, "kilograms"),
    ("Green Onions", 7, 15, "kilograms"),
    ("Bell Peppers", 7, 45, "kilograms"),
    ("Zucchini", 4, 25, "kilograms"),
    ("Mushrooms", 5, 35, "kilograms"),
    ("Broccoli", 3, 45, "kilograms"),
    ("Celery", 5, 15, "kilograms"),
    ("Water Chestnuts", 3, 15, "kilograms"),
    ("Bamboo Shoots", 3, 15, "kilograms"),
    ("Red Chili Pepper Flakes", 360, 5, "kilograms"),
    ("Dried Chilis", 720, 5, "kilograms"),
    ("Peanuts", 180, 10, "kilograms"),
    ("Sesame Seeds", 180, 5, "kilograms"),
    ("Black Pepper", 360, 5, "kilograms"),
    ("Salt", 3600, 15, "kilograms"),
    ("Chicken Broth", 5, 25, "liters"),
    ("White Rice", 180, 135, "kilograms"),
    ("Chow Mein Noodles", 360, 100, "kilograms"),
    ("Kale", 3, 25, "kilograms"),
    ("Cabbage", 5, 40, "kilograms"),
    ("Carrots", 7, 30, "kilograms"),
    ("Coca Cola Syrup", 180, 160, "liters"),
    ("Dr. Pepper Syrup", 180, 110, "liters"),
    ("Sprite Syrup", 180, 100, "liters"),
    ("Fanta Orange Syrup", 180, 60, "liters"),
    ("Dasani Water", 360, 140, "items"),
    ("Ice", 1, 25, "kilograms"),
    ("Shrimp", 180, 10, "kilograms"),
    ("Egg Roll Filling", 180, 20, "kilograms"),
    ("Eggs", 21, 200, "kilograms"),
    ("Cream Cheese", 7, 25, "kilograms"),
    ("Wonton Wrappers", 7, 4100, "items"),
    ("Egg Roll Wrappers", 7, 1800, "items"),
    ("Spring Roll Wrappers", 180, 1800, "items"),
    ("Fortune Cookies", 180, 900, "items")
]

# Data settings
RANDOM_SEED = 29  # my birthday is on the 29th
MIN_BATCHES = 1
MAX_BATCHES = 2
MIN_STOCK_FACTOR = 1  # multiplies ingredients restock_threshold
MAX_STOCK_FACTOR = 3

def build_batches(name, shelf_life, threshold, unit: str) -> list[str]:
    """Builds batches"""
    value = random.randint(MIN_BATCHES, MAX_BATCHES)

    lst = []
    for i in range(value):
        restock_date = date.today() - timedelta(days=random.randint(0, shelf_life))
        quantity = round(threshold*random.uniform(MIN_STOCK_FACTOR, MAX_STOCK_FACTOR), 2)
        if unit == "items":
            quantity = round(quantity)
        lst.append(
            f"INSERT INTO inventory (ingredient_id, quantity, restock_date) "
            f"SELECT id, {quantity}, '{restock_date}' "
            f"FROM ingredients WHERE name = '{name}';"
            )

    return lst

def main() -> None:
    """main function for generate_inventory.py"""
    random.seed(RANDOM_SEED)

    lines = []
    for name, shelf_life, threshold, unit in INGREDIENTS:
        lines.extend(build_batches(name, shelf_life, threshold, unit))

    with open(OUTPUT_PATH, "w") as file:
        file.write("\n".join(lines))


if __name__ == "__main__":
    main()
