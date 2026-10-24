package com.campus.simulator.model.resource;

import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Resource representing a portable or installed audiovisual projector.
 */
public class Projector extends Resource implements PortableResource {

    private final String resolution;
    private final boolean portable;
    private final String storageLocation;

    public Projector(String id,
                     String name,
                     String location,
                     int lumensCapacity,
                     String resolution,
                     boolean portable,
                     String storageLocation) {
        super(id, name, location, lumensCapacity, ResourceType.PROJECTOR);
        this.resolution = ValidationUtils.requireNonBlank(resolution, "resolution");
        this.portable = portable;
        this.storageLocation = ValidationUtils.requireNonBlank(storageLocation, "storageLocation");
    }

    public String getResolution() {
        return resolution;
    }

    @Override
    public boolean isPortable() {
        return portable;
    }

    @Override
    public String getStorageLocation() {
        return storageLocation;
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("Res: %s | Portable: %s | Depot: %s",
                resolution,
                portable ? "Yes" : "Fixed",
                storageLocation);
    }
}
