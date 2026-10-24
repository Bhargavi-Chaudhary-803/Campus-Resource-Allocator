package com.campus.simulator.model.resource;

import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Resource representing a scientific or computing laboratory.
 */
public class Laboratory extends Resource {

    private final String labType;
    private final String safetyLevel;

    public Laboratory(String id,
                      String name,
                      String location,
                      int capacity,
                      String labType,
                      String safetyLevel) {
        super(id, name, location, capacity, ResourceType.LABORATORY);
        this.labType = ValidationUtils.requireNonBlank(labType, "labType");
        this.safetyLevel = ValidationUtils.requireNonBlank(safetyLevel, "safetyLevel");
    }

    public String getLabType() {
        return labType;
    }

    public String getSafetyLevel() {
        return safetyLevel;
    }

    public boolean requiresSafetyClearance() {
        return safetyLevel.toUpperCase().contains("BSL") ||
                safetyLevel.equalsIgnoreCase("Hazardous") ||
                safetyLevel.equalsIgnoreCase("Level-2");
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("Type: %s | Safety: %s", labType, safetyLevel);
    }
}
