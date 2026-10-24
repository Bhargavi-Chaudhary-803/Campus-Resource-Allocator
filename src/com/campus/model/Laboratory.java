package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * Laboratory inherits from Resource. Features safety clearance requirements
 * which impact whether specific requesters (like student clubs) can book it.
 */
public class Laboratory extends Resource {

    private final String labType;
    private final boolean requiresSafetyClearance;
    private final int workstationsCount;

    public Laboratory(String id, String name, String location, int capacity,
                      String labType, boolean requiresSafetyClearance, int workstationsCount) {
        super(id, name, location, capacity, ResourceType.LABORATORY);
        this.labType = labType;
        this.requiresSafetyClearance = requiresSafetyClearance;
        this.workstationsCount = workstationsCount;
    }

    public String getLabType() {
        return labType;
    }

    public boolean requiresSafetyClearance() {
        return requiresSafetyClearance;
    }

    public int getWorkstationsCount() {
        return workstationsCount;
    }

    // [OOP - POLYMORPHISM]: Overriding abstract method
    @Override
    public String getDetails() {
        return String.format("Type: %s | Workstations: %d | Safety Clearance: %s",
                labType,
                workstationsCount,
                requiresSafetyClearance ? "MANDATORY" : "Standard");
    }

    // [OOP - POLYMORPHISM]: Labs have higher operating costs due to specialized equipment
    @Override
    public double calculateOperatingCostPerHour() {
        double cost = 40.0; // High baseline for labs
        if (requiresSafetyClearance) {
            cost += 30.0; // Safety ventilation & supervision fee
        }
        return cost;
    }
}
