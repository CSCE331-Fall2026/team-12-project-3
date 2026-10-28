package com.csce331.cashierbackend;

/**
 * Represents the supported payment methods available at checkout.
 * Maps directly to the PostgreSQL `payment_type` enum values.
 */
public enum PaymentMethod {

    /** Cash payment. */
    CASH,

    /** Credit card payment. */
    CREDIT_CARD,

    /** Debit card payment. */
    DEBIT_CARD,

    /** University dining dollars payment. */
    DINING_DOLLARS,

    /** University maroon meals payment. */
    MAROON_MEALS
}