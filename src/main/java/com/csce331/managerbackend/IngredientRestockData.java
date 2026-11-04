package com.csce331.managerbackend;

/**
 * A record representing the data for an ingredient that needs to be restocked,
 * including the ingredient itself and the amount that will expire at the end of
 * the day.
 *
 * @param ingredient    the ingredient that needs to be restocked
 * @param amountExpired the amount of the ingredient that will expire at the end
 *                      of the day
 */
public record IngredientRestockData(Ingredient ingredient, int amountExpired) {}
