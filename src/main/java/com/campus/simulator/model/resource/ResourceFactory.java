package com.campus.simulator.model.resource;

import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.util.ValidationUtils;

import java.util.Map;

/**
 * Factory class implementing the Factory Method / Simple Factory pattern
 * for creating polymorphic Resource instances.
 */
public final class ResourceFactory {

    private ResourceFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a concrete Resource based on the ResourceType and custom attributes map.
     */
    public static Resource createResource(ResourceType type,
                                          String id,
                                          String name,
                                          String location,
                                          int capacity,
                                          Map<String, Object> attributes) {
        ValidationUtils.requireNonNull(type, "type");
        Map<String, Object> attrs = attributes == null ? Map.of() : attributes;

        return switch (type) {
            case CLASSROOM -> new Classroom(
                    id,
                    name,
                    location,
                    capacity,
                    attrs.getOrDefault("roomNumber", "RM-" + id).toString(),
                    Boolean.parseBoolean(attrs.getOrDefault("hasWhiteboard", "true").toString()),
                    Boolean.parseBoolean(attrs.getOrDefault("hasProjectorScreen", "true").toString())
            );
            case LABORATORY -> new Laboratory(
                    id,
                    name,
                    location,
                    capacity,
                    attrs.getOrDefault("labType", "Computer Science").toString(),
                    attrs.getOrDefault("safetyLevel", "General").toString()
            );
            case PROJECTOR -> new Projector(
                    id,
                    name,
                    location,
                    capacity,
                    attrs.getOrDefault("resolution", "4K UHD").toString(),
                    Boolean.parseBoolean(attrs.getOrDefault("portable", "true").toString()),
                    attrs.getOrDefault("storageLocation", location).toString()
            );
            case LAPTOP_SET -> new LaptopSet(
                    id,
                    name,
                    location,
                    capacity,
                    attrs.getOrDefault("operatingSystem", "Ubuntu Linux / Win 11").toString(),
                    attrs.getOrDefault("storageLocation", location).toString()
            );
            case SPORTS_FACILITY -> new SportsFacility(
                    id,
                    name,
                    location,
                    capacity,
                    attrs.getOrDefault("sportType", "Multipurpose").toString(),
                    Boolean.parseBoolean(attrs.getOrDefault("outdoor", "false").toString())
            );
            case STUDY_ROOM -> new StudyRoom(
                    id,
                    name,
                    location,
                    capacity,
                    Boolean.parseBoolean(attrs.getOrDefault("quietZone", "true").toString()),
                    Boolean.parseBoolean(attrs.getOrDefault("hasMonitor", "true").toString())
            );
        };
    }
}
