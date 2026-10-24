package com.campus.model;

/**
 * =========================================================================
 * DESIGN PATTERN: FACTORY METHOD
 * =========================================================================
 * Encapsulates the instantiation logic for various Resource subclasses.
 * Allows client code to create polymorphic Resource instances without
 * directly coupling to concrete constructors.
 */
public class ResourceFactory {

    /**
     * Factory method with smart defaults.
     */
    public static Resource createResource(ResourceType type, String id, String name,
                                          String location, int capacity, String param1) {
        return createResource(type, id, name, location, capacity, param1, true, true);
    }

    /**
     * Factory method to create specific Resource subclasses.
     */
    public static Resource createResource(ResourceType type, String id, String name,
                                          String location, int capacity,
                                          String param1, boolean flag1, boolean flag2) {
        return switch (type) {
            case CLASSROOM -> new Classroom(
                    id, name, location, capacity,
                    param1 != null && !param1.isBlank() ? param1 : "RM-" + id,
                    flag1, // hasProjector
                    flag2  // hasSmartBoard
            );
            case LABORATORY -> new Laboratory(
                    id, name, location, capacity,
                    param1 != null && !param1.isBlank() ? param1 : "General Science",
                    flag1, // requiresSafetyClearance
                    capacity // workstations
            );
            case AUDITORIUM -> new Auditorium(
                    id, name, location, capacity,
                    flag1, // hasProAudio
                    flag2, // hasStage
                    param1 != null && !param1.isBlank() ? param1 : "800 sq ft"
            );
            case EQUIPMENT -> new Equipment(
                    id, name, location, capacity,
                    param1 != null && !param1.isBlank() ? param1 : "AV Device",
                    "SN-" + id,
                    flag1 // isPortable
            );
        };
    }
}
