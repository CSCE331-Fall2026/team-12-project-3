package com.csce331.models;

import java.math.BigDecimal;

/**
 * Recent usage and estimated time until an ingredient reaches its restock
 * threshold. A null forecast means there were no sales during the period.
 */
public class IngredientForecast {
    private final String name;
    private final BigDecimal averageDailyUsage;
    private final Long daysUntilThreshold;

    /**
     * Creates a forecast for an ingredient.
     *
     * @param name the ingredient name
     * @param averageDailyUsage average ingredient usage per day
     * @param daysUntilThreshold estimated days until the restock threshold,
     *        or null when there were no sales
     */
    public IngredientForecast(String name, BigDecimal averageDailyUsage, Long daysUntilThreshold) {
        this.name = name;
        this.averageDailyUsage = averageDailyUsage;
        this.daysUntilThreshold = daysUntilThreshold;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAverageDailyUsage() {
        return averageDailyUsage;
    }

    public Long getDaysUntilThreshold() {
        return daysUntilThreshold;
    }
}
