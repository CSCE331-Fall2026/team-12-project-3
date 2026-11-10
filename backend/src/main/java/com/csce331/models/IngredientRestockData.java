package com.csce331.models;

/**
 * Restock details for a single ingredient: the ingredient and the amount of it
 * that has expired. Instances come from
 * {@link com.csce331.managers.InventoryManager#ingredientRestockReport(InventoryLocation)}.
 *
 * @param ingredient the ingredient that needs to be restocked
 * @param amountExpired the amount that has expired
 */
public record IngredientRestockData(Ingredient ingredient, int amountExpired) {}
