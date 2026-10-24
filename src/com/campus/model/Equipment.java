package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * Equipment represents portable campus assets such as loaner laptop carts,
 * high-lumen projectors, and PA kits.
 */
public class Equipment extends Resource {

    private final String category;
    private final String serialNumber;
    private final boolean isPortable;

    public Equipment(String id, String name, String location, int capacity,
                     String category, String serialNumber, boolean isPortable) {
        super(id, name, location, capacity, ResourceType.EQUIPMENT);
        this.category = category;
        this.serialNumber = serialNumber;
        this.isPortable = isPortable;
    }

    public String getCategory() {
        return category;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public boolean isPortable() {
        return isPortable;
    }

    // [OOP - POLYMORPHISM]: Overriding abstract method
    @Override
    public String getDetails() {
        return String.format("Category: %s | S/N: %s | Portable: %s",
                category,
                serialNumber,
                isPortable ? "Hand-carried / Mobile Cart" : "Fixed");
    }

    // [OOP - POLYMORPHISM]: Equipment has lower operational power costs
    @Override
    public double calculateOperatingCostPerHour() {
        return 8.0; // Maintenance & battery charging fee
    }
}
