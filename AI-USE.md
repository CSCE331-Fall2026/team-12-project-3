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
