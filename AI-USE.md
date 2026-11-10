<<<<<<< HEAD
# AI Usage

## 1. Updating Javadocs for InventoryManager and Ingredient

**Prompt: ** Update the javadoc comments in InventoryManager.java and Ingredient.java in the managerbackend package to be accurate and reflect the current interface and functionality. Do not expose the internal workings of the functions in these javadoc comments, only what the API consumer needs to know.

**Output: ** The result of commit ID `bf517e3`.

## 2. Updating OrderManager to match InventoryManager API changes

**Prompt: ** Move the DatabaseConnector class to common and make OrderManager implement the DatabaseConnector abstract class. Then, refactor OrderManager to match the design style of the InventoryManager, and make it such that every function returns the result of the SQL statement instead of updating something in the internal state.

**Output: ** The result of commit ID `594b743`.

## 3. Pruning Dead Methods of Model Classes

**Prompt: ** Some of the classes (not the Manager ones) used in the cashierbackend and managerbackend packages contain methods not used by their respective managers. Prune these.

**Output: ** The result of commit ID `3d7df4f`.

## 4. Pruning Dead Classes

**Prompt: ** Prune any dead classes not used by the Managers as well. 

**Output: ** The result of commit ID `16f7876`.

## 5. Updating Backend Docstrings

**Prompt: ** A lot of the docstrings are outdated. Make them up to date, and only include information a package consumer would need to know. Do not include information about implementation details, unless they cause a side effect a consumer would need to know.

**Output: ** The result of commit ID `317297`.

## 6. Documenting InventoryController

**Prompt: ** Add docstrings to the undocumented functions in InventoryController and InventoryManager, and document InventoryController.

**Output: ** The result of commit ID `9c069d1`.

## 7. Explicit Throwing in InventoryManager

**Prompt: ** For all methods in InventoryManager that throw e, designate that they throw SQLException and document it accordingly in the docstring.

**Output: ** The result of commit ID `f41fc5d`.

## 8. Model Class Refactor

**Prompt: ** Refactor the managers such that the model classes (Ingredient, MenuItem, etc.) are stored in their own "models" package. Some models may be duplicated across cashierbackend and managerbackend, so merge them into one that contains all functionality both Manager classes need and remove any uneeded functionality. Fix any import and other errors that may arise as a result of this.

**Output: ** The result of commit ID `e95bc56`.

## 9. Documentation of Request Models

**Prompt: ** Add docstrings to undocumented classes, records, methods, and enum elements such that mvn javadoc:javadoc yields no errors.

**Output: ** The result of commit ID `028c762`.

## 10. Order Manager Standardization

**Prompt: **  Completely refactor OrderManager to match the style of MenuItemManager and InventoryManager. Categorized strings should be made enums instead (like menu item categories), and ensure the SQL queries match the intent of the functions (e.g., getting the most popular items should rank items by popularity). Check against init.sql for the database schema. Caught SQLExceptions should be logged with ExceptionLogger and rethrown. Extract PlacedOrder into its own class in models called Order instead of making it a nested class.

**Output: ** The result of commit ID `4fd4d0e`.

## 11. Document CustomerMenuItemController

**Prompt: ** Document all undocumented classes and methods. Ensure the documentation styles match that of InventoryManager.
 
**Output: ** The result of commit ID `d7f5daa`. 

## 12. Meeting Minutes
**Prompt: ** Format the meeting minutes to look nice in markdown.
**Output: ** `sprint-materials/sprint1-meetings.md`

## 13. Reorganize Backend

**Prompt: ** Move all *Manager classes in the backend in the *backend packages to their own unified package called managers, and update imports accordingly.

**Output: ** The result of commit ID `42c58f9`.

## 14. Document Authentication

**Prompt: ** Add docstrings to all undocumented classes such that mvn javadoc:javadoc yields no errors. Ensure that the documentation doesn't expose implementation details unless there's a state change an API consumer would need to know about.

**Output: ** The result of commit ID `da62374`.

## 15. Swagger UI

**Prompt: ** Add the necessary Swagger annotations to all of the endpoints in App describing them in accordance to the docstrings for the corresponding methods such that an API consumer knows how to use the API. Reference the Javalin OpenAPI plugin documentation online for reference: https://javalin.github.io/javalin-openapi/introduction/setup.html

**Output: ** The result of commit ID `4d6cbf5`.
