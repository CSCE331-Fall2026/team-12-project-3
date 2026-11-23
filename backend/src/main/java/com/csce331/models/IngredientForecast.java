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

    /**
     * Returns the ingredient name.
     *
     * @return the ingredient name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the recent average usage per day.
     *
     * @return average usage per day
     */
    public BigDecimal getAverageDailyUsage() {
        return averageDailyUsage;
    }

    /**
     * Returns the estimated days until the restock threshold.
     *
     * @return estimated days, or {@code null} if there has been no recent usage
     */
    public Long getDaysUntilThreshold() {
        return daysUntilThreshold;
    }
}
