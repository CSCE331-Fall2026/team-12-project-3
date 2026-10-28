import random
import datetime
from enum import Enum


class Day(Enum):
    WEEKDAY = 1
    SATURDAY = 2
    SUNDAY = 3


ORDER_TYPES = {
    "bowl": {
        "price": "8.50",
        "entrees": 1,
        "sides": 1,
        "drinks": 0,
        "utensils": ["Fork", "Clamshell Container"],
    },
    "plate": {
        "price": "10.50",
        "entrees": 2,
        "sides": 1,
        "drinks": 0,
        "utensils": ["Fork", "Clamshell Container"],
    },
    "bowl_drink": {
        "price": "11.10",
        "entrees": 1,
        "sides": 1,
        "drinks": 1,
        "utensils": ["Fork", "Clamshell Container", "Medium Cup"],
    },
    "plate_drink": {
        "price": "13.10",
        "entrees": 2,
        "sides": 1,
        "drinks": 1,
        "utensils": ["Fork", "Clamshell Container", "Medium Cup"],
    },
    "entree": {
        "price": None,
        "entrees": 1,
        "sides": 0,
        "drinks": 0,
        "utensils": ["Fork", "Takeout Carton"],
    },
    "side": {
        "price": None,
        "entrees": 0,
        "sides": 1,
        "drinks": 0,
        "utensils": ["Fork", "Takeout Carton"],
    },
    "entree_drink": {
        "price": None,
        "entrees": 1,
        "sides": 0,
        "drinks": 1,
        "utensils": ["Fork", "Takeout Carton", "Medium Cup"],
    },
    "side_drink": {
        "price": None,
        "entrees": 0,
        "sides": 1,
        "drinks": 1,
        "utensils": ["Fork", "Takeout Carton", "Medium Cup"],
    },
}


def date_to_day(date: datetime.date) -> Day:
    weekday = date.weekday()
    if weekday < 5:
        return Day.WEEKDAY
    elif weekday == 5:
        return Day.SATURDAY
    else:
        return Day.SUNDAY


def random_time(day: Day) -> str:
    match day:
        case Day.SATURDAY:
            hour = random.randint(10, 15)
        case Day.SUNDAY:
            hour = random.randint(12, 19)
        case _:
            hour = random.randint(9, 21)

    return f"{hour:02d}:{random.randint(0, 59):02d}:00"


def postgres_date(date: datetime.date) -> str:
    return date.isoformat()


def order_day(date: datetime.date):
    order_count = random.randint(200, 300)
    day_type = date_to_day(date)
    orders = []
    for _ in range(order_count):
        order_type = random.choice(list(ORDER_TYPES.keys()))
        orders.append((postgres_date(date), random_time(day_type), order_type))
    return orders


def emit_batch(f, orders):
    if not orders:
        return

    order_values = ",\n".join(
        f"    ('{time}'::time, '{date}'::date, '{order_type}')"
        for (date, time, order_type) in orders
    )

    type_values = ",\n".join(
        f"    ('{name}', {spec['entrees']}, {spec['sides']}, {spec['drinks']}, "
        f"{'NULL' if spec['price'] is None else spec['price']})"
        for name, spec in ORDER_TYPES.items()
    )

    utensil_values = ",\n".join(
        f"    ('{name}', '{utensil}')"
        for name, spec in ORDER_TYPES.items()
        for utensil in spec["utensils"]
    )

    f.write(f"""
WITH batch(order_time, order_date, order_type) AS (
    VALUES
{order_values}
),
type_spec(order_type, entrees, sides, drinks, price) AS (
    VALUES
{type_values}
),
type_utensils(order_type, utensil_name) AS (
    VALUES
{utensil_values}
),
batch_rows AS (
    SELECT batch.*, s.entrees, s.sides, s.drinks, s.price,
           row_number() OVER () AS batch_no
    FROM batch
    JOIN type_spec s USING (order_type)
),
picks AS (
    SELECT br.batch_no, 'entree'::menu_category AS item_category, g AS slot
    FROM batch_rows br, LATERAL generate_series(1, br.entrees) g
    UNION ALL
    SELECT br.batch_no, 'side'::menu_category, g
    FROM batch_rows br, LATERAL generate_series(1, br.sides) g
    UNION ALL
    SELECT br.batch_no, 'drink'::menu_category, g
    FROM batch_rows br, LATERAL generate_series(1, br.drinks) g
),
picked_items AS (
    SELECT p.batch_no, m.id AS menu_item_id, m.price AS item_price
    FROM picks p
    JOIN LATERAL (
        SELECT id, price
        FROM menu_items
        WHERE item_category = p.item_category
        AND is_active = TRUE
        ORDER BY random()
        LIMIT 1
    ) m ON true
),
order_totals AS (
    SELECT br.batch_no, br.order_time, br.order_date, br.order_type,
           COALESCE(br.price, SUM(pi.item_price)) AS total_price
    FROM batch_rows br
    LEFT JOIN picked_items pi ON pi.batch_no = br.batch_no
    GROUP BY br.batch_no, br.order_time, br.order_date, br.order_type, br.price
),
inserted_orders AS (
    INSERT INTO orders (order_time, order_date, price)
    SELECT order_time, order_date, total_price
    FROM order_totals
    ORDER BY batch_no
    RETURNING id
),
order_ids AS (
    SELECT id, row_number() OVER (ORDER BY id) AS batch_no
    FROM inserted_orders
),
menu_insert AS (
    INSERT INTO order_menu (order_id, menu_item_id, quantity)
    SELECT oi.id, pi.menu_item_id, 1
    FROM picked_items pi
    JOIN order_ids oi USING (batch_no)
    RETURNING order_id
)
INSERT INTO order_utensils (order_id, utensil_id, quantity)
SELECT oi.id, u.id, 1
FROM batch_rows br
JOIN order_ids oi USING (batch_no)
JOIN type_utensils tu ON tu.order_type = br.order_type
JOIN utensils u ON u.name = tu.utensil_name;
""")


def main():
    start_date = datetime.date(2025, 9, 26)
    end_date = datetime.date.today()
    delta = datetime.timedelta(days=1)
    with open("db/orders.sql", "w") as f:
        commit_index = 0
        f.write("BEGIN;\n")
        current_date = start_date
        while current_date <= end_date:
            orders = order_day(current_date)
            emit_batch(f, orders)

            if commit_index % 10 == 0 and commit_index != 0:
                f.write("COMMIT;\nBEGIN;\n")

            commit_index += 1
            current_date += delta

        peak_day_1 = datetime.date(2026, 9, 19)
        peak_day_2 = datetime.date(2026, 9, 12)
        emit_batch(f, order_day(peak_day_1))
        emit_batch(f, order_day(peak_day_2))

        f.write("COMMIT;\n")


if __name__ == "__main__":
    main()
