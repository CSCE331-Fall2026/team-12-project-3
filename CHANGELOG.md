# Changelog

Highlights for Team 12 Project 3 (Panda Express ordering and manager portal).

## Sprint 1

- Stood up the Javalin backend ported from Project 2, including JDBC/PostgreSQL
  connectivity, model and request classes, and managers for orders, menu items,
  and inventory.
- Implemented manager inventory operations: viewing stock by location, setting
  restock quantities and thresholds, and ending the day.
- Added the inventory, menu, ordering, and customer menu controllers and
  registered them with the API; standardized request objects across endpoints.
- Added the accounts table, authentication manager and controller, and Google
  OAuth2 sign-in with customer/manager role selection.
- Scaffolded the Vite + React + TypeScript frontend with a sign-in screen, role
  selection, and a manager inventory table with restocking. Customer view was a
  placeholder at this point.
- Added Swagger UI/OpenAPI docs, a Javadoc generation script, the Project 2
  database scripts (schema, teardown, validation, sample data), and AI-use
  documentation.

## Sprint 2

- Containerized the backend with a Dockerfile and deployed it on Render using
  environment-based configuration; restricted CORS to the public Vercel
  frontend.
- Built predictive restocking: per-item days-until-restock estimates for
  ingredients and utensils, filterable by location, with supporting API
  endpoints and OpenAPI documentation. Reworked SQL access to use prepared
  statements and try-with-resources.
- Expanded the manager interface with restock status and a clearer additive
  restock flow, fixing the utensil restocking bug and a divide-by-zero error.
- Added the manager sales report with date-range selection, a daily revenue
  trend, and highest-revenue menu items.
- Built the customer ordering flow: category menu browsing, cart and order
  summary, and checkout with tipping and payment-method selection, connected to
  the backend. Fixed menu item serialization so orders reach the server, and
  stopped card numbers from displaying in the UI.
- Made the interface responsive across desktop and mobile sizes, added the tab
  icon, and refactored the frontend into screens, components, hooks, and API
  modules under a new style guide.
- Conducted and documented user study 1, and updated sprint meeting notes,
  sprint goals, the README, and AI-use documentation.
