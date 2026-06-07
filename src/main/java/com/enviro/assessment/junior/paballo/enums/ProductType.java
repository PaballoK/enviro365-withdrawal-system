package com.enviro.assessment.junior.paballo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductType {

    RETIREMENT("Retirement"),
    SAVINGS("Savings"),
    INVESTMENT("Investment");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ProductType fromValue(String value) {
        if (value == null) {
            return null;
        }

        for (ProductType productType : ProductType.values()) {

            if (productType.description.equalsIgnoreCase(value)) {
                return productType;
            }

            if (productType.name().equalsIgnoreCase(value)) {
                return productType;
            }
        }

        throw new IllegalArgumentException("Unknown product type: " + value);
    }
}