package com.campus.simulator.model.resource;

import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Resource representing a batch of mobile laptops for exams, workshops, or clubs.
 */
public class LaptopSet extends Resource implements PortableResource {

    private final int laptopCount;
    private final String operatingSystem;
    private final String storageLocation;

    public LaptopSet(String id,
                     String name,
                     String location,
                     int laptopCount,
                     String operatingSystem,
                     String storageLocation) {
        super(id, name, location, laptopCount, ResourceType.LAPTOP_SET);
        this.laptopCount = ValidationUtils.requirePositive(laptopCount, "laptopCount");
        this.operatingSystem = ValidationUtils.requireNonBlank(operatingSystem, "operatingSystem");
        this.storageLocation = ValidationUtils.requireNonBlank(storageLocation, "storageLocation");
    }

    public int getLaptopCount() {
        return laptopCount;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    @Override
    public boolean isPortable() {
        return true;
    }

    @Override
    public String getStorageLocation() {
        return storageLocation;
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("%d Laptops (%s) | Stored at %s", laptopCount, operatingSystem, storageLocation);
    }
}
