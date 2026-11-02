# AI Usage

## 1. Updating Javadocs for InventoryManager and Ingredient

**Prompt: ** Update the javadoc comments in InventoryManager.java and Ingredient.java in the managerbackend package to be accurate and reflect the current interface and functionality. Do not expose the internal workings of the functions in these javadoc comments, only what the API consumer needs to know.

**Output: ** The result of commit ID `bf517e3`.

## 2. Updating OrderManager to match InventoryManager API changes

**Prompt: ** Move the DatabaseConnector class to common and make OrderManager implement the DatabaseConnector abstract class. Then, refactor OrderManager to match the design style of the InventoryManager, and make it such that every function returns the result of the SQL statement instead of updating something in the internal state.

**Output: ** The result of commit ID `594b743`.
